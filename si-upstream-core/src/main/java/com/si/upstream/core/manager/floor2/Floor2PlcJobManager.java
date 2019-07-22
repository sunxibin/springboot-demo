package com.si.upstream.core.manager.floor2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.si.upstream.common.enums.floor2.Floor2PlcJobTaskStatus;
import com.si.upstream.common.enums.floor2.Floor2PlcJobTaskType;
import com.si.upstream.common.util.ObjectMappers;
import com.si.upstream.common.util.SetUtil;
import com.si.upstream.core.floor2.Floor2WcsStandardJob;
import com.si.upstream.core.floor2.WcsStandardResponse;
import com.si.upstream.dal.das.floor2.Floor2BucketDas;
import com.si.upstream.dal.das.floor2.Floor2PlcJobTaskDas;
import com.si.upstream.dal.entity.floor2.Floor2Bucket;
import com.si.upstream.dal.entity.floor2.Floor2PlcJobTask;
import com.si.upstream.model.plc.PlcFrameData;
import com.si.upstream.model.plc.PlcJobMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class Floor2PlcJobManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Floor2PlcJobManager.class);

    @Resource
    private Floor2PlcJobTaskDas floor2PlcJobTaskDas;

    @Resource
    private Floor2BucketDas floor2BucketDas;

    @Resource
    private Floor2WcsStandardJob floor2WcsStandardJob;

    public PlcJobMessage hand(PlcFrameData<PlcJobMessage> plcFrameData) {
        Result<?> res = Result.fail("can't handle this type plc data frame.");
        int type = plcFrameData.getType();
        PlcJobMessage message = plcFrameData.getBody();
        switch (type) {
            //炒料区到灌料区任务下发接口
            case 1: res = toProducer(message); break;
            //灌料区到炒料区任务下发接口
            case 2: res = toConsumer(message); break;
            //AGV一键归巢接口
            case 6: res = handlerForAgvHoming(message); break;
            //任务取消接口
            case 7: res = handlerForJobCancel(message); break;
            default:
                LOGGER.warn("Floor2PlcJobManager receive a wrong plc order: " + ObjectMappers.mustWriteValue(plcFrameData));
                break;
        }
        return PlcJobMessage.result(plcFrameData, res.isSuccess()).set_inner_debug_error_desc(res.msg);
    }

    /**
     * 1、炒料区到灌料区任务下发
     */
    private Result<?> toProducer(PlcJobMessage message) {
        Result<Floor2Bucket> bucket = toProducerStationBucket(message);
        if(bucket.isFail()) {
            return bucket;
        }
        return createPlcTask(message, bucket.data, Floor2PlcJobTaskType.PRODUCE, null);
    }

    /**
     * 2、灌料区到炒料区任务下发
     */
    private Result<?> toConsumer(PlcJobMessage message) {
        Result<Floor2Bucket> bucket = toConsumerStationBucket(message);
        if(bucket.isFail()) {
            return bucket;
        }
        return createPlcTask(message, bucket.data, Floor2PlcJobTaskType.CONSUME, bucket.data.getStationPointCode());
    }

    private Result<?> createPlcTask(PlcJobMessage message, Floor2Bucket bucket, Floor2PlcJobTaskType taskType, String targetPointCode) {
        synchronized (bucket.getBucketCode().intern()) {
            Floor2PlcJobTask floor2PlcJobTask = new Floor2PlcJobTask();
            floor2PlcJobTask.setStatus(Floor2PlcJobTaskStatus.WAIT_DISPATCH.name());
            floor2PlcJobTask.setBucketCode(bucket.getBucketCode());
            floor2PlcJobTask.setPlcJobType(taskType.name());
            floor2PlcJobTask.setSourceStationCode(message.getSourceStationCode());
            floor2PlcJobTask.setSourcePointCode(message.getSourcePointCode());
            floor2PlcJobTask.setTargetStationCode(message.getTargetStationCode());
            floor2PlcJobTask.setZoneCode(message.getZoneCode());
            floor2PlcJobTask.setWarehouseCode(message.getWarehouseCode());
            if (targetPointCode != null) floor2PlcJobTask.setTargetPointCode(targetPointCode);

            Floor2PlcJobTask last = getLastActiveTask(floor2PlcJobTask.getBucketCode());
            if (last != null && !floor2PlcJobTask.getSourceStationCode().equals(last.getTargetStationCode())) {
                String errMsg = "task can't crate, because the last task targetStationCode is not equals the sourceStationCode, curTask: " + ObjectMappers.mustWriteValue(floor2PlcJobTask) + ", last: " + ObjectMappers.mustWriteValue(last);
                LOGGER.warn(errMsg);
                return Result.fail("", errMsg);
            }

            Result failed = Result.fail().msg("plcTask insert failed: message: " + ObjectMappers.mustWriteValue(message) + ", bucket: " + ObjectMappers.mustWriteValue(bucket) + ", floor2PlcJobTask: " + ObjectMappers.mustWriteValue(floor2PlcJobTask));
            try {
                return floor2PlcJobTaskDas.insert(floor2PlcJobTask) > 0 ? Result.success() : failed;
            } catch (Exception e) {
                LOGGER.warn(failed.msg + ", error: " + e.getLocalizedMessage(), e);
                return failed;
            }
        }
    }

    public Floor2PlcJobTask getLastActiveTask(String bucketCode) {
        Floor2PlcJobTask whereItem = new Floor2PlcJobTask();
        whereItem.setBucketCode(bucketCode);
        QueryWrapper wrapper = new QueryWrapper(whereItem);
        wrapper.orderByDesc("id");
        wrapper.ne("status", Floor2PlcJobTaskStatus.CANCEL.name());
        return SetUtil.first(floor2PlcJobTaskDas.queryByCondition(new Page<>(0, 1), wrapper));
    }

    /**
     * 6、AGV一键归巢
     */
    private Result<?> handlerForAgvHoming(PlcJobMessage message) {
        if(message == null || StringUtils.isAnyBlank(message.getWarehouseCode(), message.getZoneCode())) {
            Result failed = Result.fail("", "handlerForAgvHoming wrong plc message: " + ObjectMappers.mustWriteValue(message));
            LOGGER.warn(failed.msg);
            return failed;
        }
        WcsStandardResponse res = floor2WcsStandardJob.robotAllBack(message);
        return res.ok() ? Result.success() : Result.fail("", "wcs interface res: " + res.getErrorCode());
    }

    /**
     * 7、任务取消
     */
    private Result<?> handlerForJobCancel(PlcJobMessage message) {
        if(message == null || StringUtils.isAnyBlank(message.getWarehouseCode(), message.getZoneCode(), message.getSourceStationCode(), message.getSourcePointCode())) {
            String errMsg = "handlerForJobCancel wrong plc message: " + ObjectMappers.mustWriteValue(message);
            LOGGER.warn(errMsg);
            return Result.fail("", errMsg);
        }
        Floor2PlcJobTask cond = new Floor2PlcJobTask();
        cond.setWarehouseCode(message.getWarehouseCode());
        cond.setZoneCode(message.getZoneCode());
        cond.setSourceStationCode(message.getSourceStationCode());
        cond.setSourcePointCode(message.getSourcePointCode());

        List<Floor2PlcJobTask> willCancelList = floor2PlcJobTaskDas.queryByCondition(cond);
        Set<String> buckets = willCancelList.stream().map(Floor2PlcJobTask::getBucketCode).filter(e -> StringUtils.isNotBlank(e)).collect(Collectors.toSet());

        try {
            for(String bucketCode : buckets) {
                cancelBucketTasks(cond, bucketCode);
            }
            return Result.success();
        } catch (Exception e) {
            String errMsg = "handlerForJobCancel update failed: message: " + ObjectMappers.mustWriteValue(message) + ", error: " + e.getLocalizedMessage();
            LOGGER.warn(errMsg, e);
            return Result.fail("", errMsg);
        }
    }

    @Transactional
    public void cancelBucketTasks(Floor2PlcJobTask queryCondEntity, String bucketCode) {
        synchronized (bucketCode.intern()) {
            Floor2PlcJobTask updateItem = new Floor2PlcJobTask();
            updateItem.setStatus(Floor2PlcJobTaskStatus.CANCEL.name());

            UpdateWrapper where = new UpdateWrapper();
            where.setEntity(queryCondEntity);
            where.eq("bucket_code", bucketCode);
            where.eq( "status", Floor2PlcJobTaskStatus.WAIT_DISPATCH.name());

            QueryWrapper wrapper = new QueryWrapper();
            wrapper.setEntity(queryCondEntity);
            wrapper.eq("bucket_code", bucketCode);
            wrapper.eq("status", Floor2PlcJobTaskStatus.WAIT_DISPATCH.name());

            List<Floor2PlcJobTask> bucketWillCancelList = floor2PlcJobTaskDas.queryByCondition(wrapper);
            Long minForGt = null;
            for(Floor2PlcJobTask task : bucketWillCancelList) {
                if(minForGt == null || task.getId() < minForGt) {
                    minForGt = task.getId();
                }
            }

            int row = floor2PlcJobTaskDas.update(updateItem, where);
            if(row > 0 && minForGt != null) {
                UpdateWrapper gtUpdateWhere = new UpdateWrapper();
                gtUpdateWhere.eq("bucket_code", bucketCode);
                gtUpdateWhere.eq("status", Floor2PlcJobTaskStatus.WAIT_DISPATCH.name());
                gtUpdateWhere.gt("id", minForGt);
                row += floor2PlcJobTaskDas.update(updateItem, gtUpdateWhere);
            }
            LOGGER.info("handlerForJobCancel: cancelBucketTasks minForGt: " + minForGt + ", queryCondEntity: " + ObjectMappers.mustWriteValue(queryCondEntity) + ", bucketCode: " + bucketCode + " change row: " + row);
        }
    }

//    public Floor2PlcJobTask getLastCanceledTask(String bucketCode) {
//        QueryWrapper wrapper = new QueryWrapper();
//        wrapper.eq("bucketCode", bucketCode);
//        wrapper.orderByDesc("id");
//        wrapper.eq("status", Floor2PlcJobTaskStatus.CANCEL.name());
//        return SetUtil.first(floor2PlcJobTaskDas.queryByCondition(new Page<>(0, 1), wrapper));
//    }

    Floor2Bucket stationBucket(String stationCode, String pointCode, String stationInUse, String pointInUse) {
        if(StringUtils.isBlank(stationCode) && StringUtils.isBlank(stationInUse)) {
            return null;
        }
        Floor2Bucket cond = new Floor2Bucket();
        if(stationCode != null) cond.setStationCode(stationCode);
        if(pointCode != null)   cond.setStationPointCode(pointCode);
        if(stationInUse != null)   cond.setStationInUse(stationInUse);
        if(pointInUse != null)   cond.setPointInUse(pointInUse);
        return floor2BucketDas.getByCondition(cond);
    }

    Result<Floor2Bucket> toConsumerStationBucket(PlcJobMessage message) {
        if(message == null || StringUtils.isBlank(message.getWarehouseCode()) ||
                StringUtils.isBlank(message.getZoneCode()) || StringUtils.isBlank(message.getSourceStationCode()) ||
                StringUtils.isBlank(message.getSourcePointCode()) || StringUtils.isBlank(message.getTargetStationCode())) {
            Result failed = Result.fail("", "toConsumerStationBucket wrong plcJobMessage: " + ObjectMappers.mustWriteValue(message));
            LOGGER.warn(failed.msg);
            return failed;
        }
        Floor2Bucket bucket = stationBucket(null, null, message.getSourceStationCode(), message.getSourcePointCode());
        if(bucket == null || StringUtils.isBlank(bucket.getBucketCode())) {
            Result failed = Result.fail("", "Floor2PlcJobManager.toConsumerStationBucket bucket data error: message: " + ObjectMappers.mustWriteValue(message) + ", bucket: " + ObjectMappers.mustWriteValue(bucket));
            LOGGER.warn(failed.msg);
            return null;
        }
        return Result.success(bucket);
    }

    Result<Floor2Bucket> toProducerStationBucket(PlcJobMessage message) {
        if(message == null || StringUtils.isBlank(message.getWarehouseCode()) ||
                StringUtils.isBlank(message.getZoneCode()) || StringUtils.isBlank(message.getSourceStationCode()) ||
                StringUtils.isBlank(message.getSourceStationCode()) || StringUtils.isBlank(message.getTargetStationCode())) {
            Result failed = Result.fail("", "toProducerStationBucket wrong plcJobMessage: " + ObjectMappers.mustWriteValue(message));
            LOGGER.warn(failed.msg);
            return failed;
        }
        Floor2Bucket bucket = stationBucket( message.getSourceStationCode(), message.getSourcePointCode(), null, null);
        if(bucket == null || StringUtils.isBlank(bucket.getBucketCode())) {
            Result failed = Result.fail("", "Floor2PlcJobManager.toProducerStationBucket bucket data error: message: " + ObjectMappers.mustWriteValue(message) + ", bucket: " + ObjectMappers.mustWriteValue(bucket));
            LOGGER.warn(failed.msg);
            return failed;
        }
        return Result.success(bucket);
    }

    public static class Result<T> {

        public int code;
        public T data;
        public String err;
        public String msg;

        public Result<T> resSuccess(){
            this.code = 0;
            return this;
        }

        public Result<T> resFail(){
            this.code = -1;
            return this;
        }

        public boolean isSuccess(){
            return code == 0;
        }

        public boolean isFail(){
            return !isSuccess();
        }

        public Result<T> data(T data){
            this.data = data;
            return this;
        }

        public Result<T> msg(String msg){
            this.msg = msg;
            return this;
        }

        public Result<T> err(String err){
            this.err = err;
            return this;
        }

        public static <T> Result<T> success(){
            return create(0, null, null, null);
        }

        public static <T> Result<T> success(T data){
            return create(0, data, null, null);
        }

        public static <T> Result<T> success(String msg, T data){
            return create(0, data, null, msg);
        }

        public static <T> Result<T> fail(){
            return create(-1, null, null, null);
        }

        public static <T> Result<T> fail(int code){
            return create(code, null, null, null);
        }

        public static <T> Result<T> fail(String err){
            return create(-1, null, err, null);
        }

        public static <T> Result<T> fail(int code, String err){
            return create(code, null, err, null);
        }

        public static <T> Result<T> fail(String err, String msg){
            return create(-1, null, err, msg);
        }

        public static <T> Result<T> failData(String err, T data){
            return create(-1, data, err, null);
        }

        public static <T> Result<T> create(int code, T data, String err, String msg){
            Result<T> res = new Result<T>();
            res.code = code;
            res.data = data;
            res.err = err;
            res.msg = msg;
            return res;
        }
    }

}
