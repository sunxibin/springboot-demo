package com.si.upstream.core.manager.floor2;

import com.si.upstream.common.enums.JobResult;
import com.si.upstream.common.enums.floor2.*;
import com.si.upstream.common.util.ObjectMappers;
import com.si.upstream.common.util.UrlUtils;
import com.si.upstream.dal.das.floor2.Floor2BucketDas;
import com.si.upstream.dal.das.floor2.Floor2PlcJobTaskDas;
import com.si.upstream.dal.das.floor2.Floor2StationPointDas;
import com.si.upstream.dal.das.floor2.Floor2WcsMoveJobDas;
import com.si.upstream.dal.entity.floor2.Floor2Bucket;
import com.si.upstream.dal.entity.floor2.Floor2PlcJobTask;
import com.si.upstream.dal.entity.floor2.Floor2WcsMoveJob;
import com.si.upstream.model.plc.PlcJobMessage;
import com.si.upstream.model.wcs.job.JobCompleteVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class Floor2WcsMessageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Floor2WcsMessageManager.class);

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private Floor2WcsMoveJobDas floor2WcsMoveJobDas;

    @Resource
    private Floor2PlcJobTaskDas floor2PlcJobTaskDas;

    @Resource
    private Floor2BucketDas floor2BucketDas;

    @Resource
    private Floor2StationPointDas floor2StationPointDas;

    Floor2Bucket bucket(String bucketCode) {
        if(StringUtils.isBlank(bucketCode)) {
            return null;
        }
        Floor2Bucket cond = new Floor2Bucket();
        cond.setBucketCode(bucketCode);
        return floor2BucketDas.getByCondition(cond);
    }

    public boolean processJobComplete(JobCompleteVO jobCompleteVO) {
        if(jobCompleteVO == null || StringUtils.isAnyBlank(jobCompleteVO.getRobotJobId())) {
            LOGGER.warn("processJobComplete: wcs jobComleteVo is error: " + ObjectMappers.mustWriteValue(jobCompleteVO));
            return false;
        }

        Floor2PlcJobTask reportTask = updateMoveSuccessState(jobCompleteVO.getRobotJobId());
        if(reportTask != null) {
            reportCompleted(reportTask);
        }
        return true;
    }

    @Transactional
    Floor2PlcJobTask updateMoveSuccessState(String robotJobId) {
        if(StringUtils.isBlank(robotJobId)) return null;
        Floor2WcsMoveJob cond = new Floor2WcsMoveJob();
        cond.setWcsJobId(robotJobId);
        Floor2WcsMoveJob floor2WcsMoveJob = floor2WcsMoveJobDas.getByCondition(cond);
        if(floor2WcsMoveJob == null || floor2WcsMoveJob.getPlcTaskId() == null) {
            LOGGER.warn("processJobComplete: floor2WcsMoveJob in error status, robotJobId: " + robotJobId + ", moveJob: " + ObjectMappers.mustWriteValue(floor2WcsMoveJob));
            return null;
        }

        Floor2WcsPlcJobMoveType moveType = Floor2WcsPlcJobMoveType.getByName(floor2WcsMoveJob.getPlcJobType());

        Floor2PlcJobTask floor2PlcJobTask = floor2PlcJobTaskDas.queryById(floor2WcsMoveJob.getPlcTaskId());
        if(floor2PlcJobTask == null || moveType.plcTaskStatus != Floor2PlcJobTaskStatus.getByName(floor2PlcJobTask.getStatus())) {
            LOGGER.warn("processJobComplete: floor2PlcJobTask in error status: " + ObjectMappers.mustWriteValue(floor2PlcJobTask) + ", floor2WcsMoveJob: " + ObjectMappers.mustWriteValue(floor2WcsMoveJob));
            return null;
        }

        Floor2WcsMoveJob updItem = new Floor2WcsMoveJob();
        updItem.setId(floor2WcsMoveJob.getId());
        updItem.setStatus(Floor2WcsMoveJobStatus.DONE.name());

        floor2WcsMoveJobDas.update(updItem);

        String stationInUse = moveType == Floor2WcsPlcJobMoveType.DIRECT || moveType == Floor2WcsPlcJobMoveType.TAIL ? floor2PlcJobTask.getTargetStationCode() : "";
        String pointInUse = floor2WcsMoveJob.getPointCode();
        Floor2Bucket bucket = bucket(floor2PlcJobTask.getBucketCode());
        if(bucket != null) {
            Floor2Bucket updBucket = new Floor2Bucket();
            updBucket.setId(bucket.getId());
            updBucket.setPointInUse(pointInUse);
            updBucket.setStationInUse(stationInUse);
            floor2BucketDas.update(updBucket);
        }

        Floor2PlcJobTask reportTask = floor2PlcJobTask;
        switch (moveType) {
            case HEAD: reportTask = null;
            case DIRECT:
            case TAIL:
                Floor2PlcJobTask fjuUpdate = new Floor2PlcJobTask();
                fjuUpdate.setId(floor2PlcJobTask.getId());
                fjuUpdate.setStatus(moveType.plcTaskNextStatus.name());
                floor2PlcJobTaskDas.update(fjuUpdate);
                updatePointCodeStatus(floor2PlcJobTask, moveType);
                return reportTask;
            default:
                LOGGER.warn("updateMoveSuccessState moveType is illegal: " + moveType);
            return null;
        }
    }

    void updatePointCodeStatus(Floor2PlcJobTask task, Floor2WcsPlcJobMoveType moveType) {
        String unlock = null;
        switch (moveType) {
            case HEAD:
            case DIRECT: unlock = task.getSourcePointCode(); break;
            case TAIL: unlock = task.getCachePointCode(); break;
        }
        if(StringUtils.isNotBlank(unlock)) {
            floor2StationPointDas.updateOccupiedState(unlock, Floor2PointOccupyStatus.USABLE.name());
        }
    }

    /**
     * 任务完成上报
     */
    private void reportCompleted(Floor2PlcJobTask task) {
        String url = UrlUtils.getReportCompletedUrl();
        PlcJobMessage message = new PlcJobMessage();
        message.setWarehouseCode(task.getWarehouseCode());
        message.setZoneCode(task.getZoneCode());

        String consumerStationCode = Floor2PlcJobTaskType.getByName(task.getPlcJobType()) == Floor2PlcJobTaskType.PRODUCE ? task.getSourceStationCode() : task.getTargetStationCode();
        message.setStationCode(task.getTargetStationCode());
        message.setSourcePointCode(task.getSourcePointCode());
        message.setTargetPointCode(task.getTargetPointCode());

        message.setJobResult(JobResult.success.getCode());
        String result = null;
        try {
            result = restTemplate.postForObject(url, message, String.class);
        } catch (Exception e) {
            LOGGER.warn("reportCompleted failed: " + e.getLocalizedMessage(), e);
        }

        LOGGER.info("reportCompleted: task: " + ObjectMappers.mustWriteValue(task) + ", result: " + result);
    }

}
