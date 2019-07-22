package com.si.upstream.core.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.si.upstream.common.enums.floor2.*;
import com.si.upstream.common.util.IdCreater;
import com.si.upstream.common.util.ObjectMappers;
import com.si.upstream.common.util.SetUtil;
import com.si.upstream.core.floor2.Floor2WcsStandardJob;
import com.si.upstream.core.floor2.WcsStandardResponse;
import com.si.upstream.dal.das.floor2.Floor2BucketDas;
import com.si.upstream.dal.das.floor2.Floor2PlcJobTaskDas;
import com.si.upstream.dal.das.floor2.Floor2StationPointDas;
import com.si.upstream.dal.das.floor2.Floor2WcsMoveJobDas;
import com.si.upstream.dal.entity.floor2.Floor2PlcJobTask;
import com.si.upstream.dal.entity.floor2.Floor2StationPoint;
import com.si.upstream.dal.entity.floor2.Floor2WcsMoveJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class Floor2Task extends BaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(Floor2Task.class);

    @Resource
    private Floor2WcsStandardJob floor2WcsStandardJob;

    @Resource
    private Floor2PlcJobTaskDas floor2PlcJobTaskDas;

    @Resource
    private Floor2BucketDas floor2BucketDas;

    @Resource
    private Floor2StationPointDas floor2StationPointDas;

    @Resource
    private Floor2WcsMoveJobDas floor2WcsMoveJobDas;

    @Value("${task.cron.floor2}")
    private String cron;

    @Override
    protected String getCron() {
        return cron;
    }

    @Override
    protected boolean isOpen() {
        return true;
    }

    public static Floor2PlcJobTask cond(Floor2PlcJobTaskStatus status) {
        Floor2PlcJobTask taskCond = new Floor2PlcJobTask();
        taskCond.setStatus(status.name());
        return taskCond;
    }

    public static Floor2StationPoint cond( Floor2PointType type, Floor2PointOccupyStatus status) {
        Floor2StationPoint pointCond = new Floor2StationPoint();
        pointCond.setStatus(status.name());
        pointCond.setPointType(type.name());
        return pointCond;
    }


    @Override
    protected void executeTask() {
        cache2Reach();
        deptJob();
        wcsRotJobMove();
    }

    public static final int DEFAULT_MOVE_TASK_COUNT = 100;

    List<Floor2PlcJobTask> queryPlcTasks(Floor2PlcJobTaskStatus status) {
        return floor2PlcJobTaskDas.queryByCondition(new Page<>(0, DEFAULT_MOVE_TASK_COUNT), cond(status));
    }

    Map<String, List<Floor2PlcJobTask>> statusPointMap(Floor2PlcJobTaskStatus status) {
        Map<String, List<Floor2PlcJobTask>> stationTaskMap = new HashMap<>();
        for(Floor2PlcJobTask floor2PlcJobTask : queryPlcTasks(status)) {
            SetUtil.pick(stationTaskMap, floor2PlcJobTask.getTargetStationCode(), floor2PlcJobTask);
        }
        return stationTaskMap;
    }

    List<Floor2StationPoint> pointList(Floor2PointType floor2PointType, Floor2PointOccupyStatus status) {
        return floor2StationPointDas.queryByCondition(cond(floor2PointType, status));
    }

    Map<String, List<Floor2StationPoint>> usableStationPointMap(Floor2PointType floor2PointType, Floor2PointOccupyStatus status) {
        Map<String, List<Floor2StationPoint>> stationPointMap = new HashMap<>();
        for(Floor2StationPoint floor2StationPoint : pointList(floor2PointType, status)) {
            SetUtil.pick(stationPointMap, floor2StationPoint.getStation(), floor2StationPoint);
        }
        return stationPointMap;
    }

    public void deptJob() {
        Map<String, List<Floor2PlcJobTask>> stationWaitDeptTaskMap = statusPointMap(Floor2PlcJobTaskStatus.WAIT_DISPATCH);
        Map<String, List<Floor2StationPoint>> stationPointMap = usableStationPointMap(Floor2PointType.STATION, Floor2PointOccupyStatus.USABLE);
        List<Floor2StationPoint> usableCachePoints = pointList(Floor2PointType.CACHE, Floor2PointOccupyStatus.USABLE);

        for(Map.Entry<String, List<Floor2PlcJobTask>> e : stationWaitDeptTaskMap.entrySet()) {
            List<Floor2StationPoint> usableStationPoints = stationPointMap.get(e.getKey());
            for(Floor2PlcJobTask floor2PlcJobTask : e.getValue()) {
                if(!CollectionUtils.isEmpty(usableStationPoints)) {
                    doDeptJob(usableStationPoints, floor2PlcJobTask, Floor2WcsPlcJobMoveType.DIRECT);
                } else if(!CollectionUtils.isEmpty(usableCachePoints)) {
                    if(Floor2PlcJobTaskType.PRODUCE == Floor2PlcJobTaskType.getByName(floor2PlcJobTask.getPlcJobType())) {
                        doDeptJob(usableCachePoints, floor2PlcJobTask, Floor2WcsPlcJobMoveType.HEAD);
                    }
                } else {
                    break;
                }
            }
        }
    }

    public void doDeptJob(List<Floor2StationPoint> usablePoints, Floor2PlcJobTask floor2PlcJobTask, Floor2WcsPlcJobMoveType moveType) {
        if(CollectionUtils.isEmpty(usablePoints) || floor2PlcJobTask == null) {
            return ;
        }
        Floor2StationPoint point = SetUtil.first(usablePoints);
        try {
            if(createWcsMoveTask(floor2PlcJobTask, point, moveType)){
                usablePoints.remove(point);
            }
        } catch (Exception ex) {
            LOGGER.info("doDeptJob throws an exception: status: " + moveType.plcTaskStatus + ", moveType: " + moveType + ", task: " + ObjectMappers.mustWriteValue(floor2PlcJobTask) + ", point: " + ObjectMappers.mustWriteValue(point) + ex.getLocalizedMessage(), ex);
        }
    }

    public void cache2Reach() {
        Map<String, List<Floor2PlcJobTask>> stationCacheTaskMap = statusPointMap(Floor2PlcJobTaskStatus.CACHED);
        Map<String, List<Floor2StationPoint>> stationPointMap = usableStationPointMap(Floor2PointType.STATION, Floor2PointOccupyStatus.USABLE);

        for(Map.Entry<String, List<Floor2PlcJobTask>> e : stationCacheTaskMap.entrySet()) {
            List<Floor2StationPoint> usablePoints = stationPointMap.get(e.getKey());
            for(Floor2PlcJobTask floor2PlcJobTask : e.getValue()) {
                if(CollectionUtils.isEmpty(usablePoints)) {
                    break;
                }
                Floor2StationPoint point = SetUtil.first(usablePoints);
                try {
                    if (createWcsMoveTask(floor2PlcJobTask, point, Floor2WcsPlcJobMoveType.TAIL)) {
                        usablePoints.remove(point);
                    }
                } catch (Exception ex) {
                    LOGGER.info("cache2Reach throws an exception: task: " + ObjectMappers.mustWriteValue(floor2PlcJobTask) + ", point: " + ObjectMappers.mustWriteValue(point) + ex.getLocalizedMessage(), ex);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createWcsMoveTask(Floor2PlcJobTask task, Floor2StationPoint stationPoint, Floor2WcsPlcJobMoveType moveType) throws Exception {
        String wcsRobotJobId = IdCreater.getInnerJobId();
        Floor2PlcJobTask updateItem = new Floor2PlcJobTask();

        updateItem.setId(task.getId());
        updateItem.setStatus(moveType.plcTaskStatus.name());

        if(moveType == Floor2WcsPlcJobMoveType.HEAD) {
            updateItem.setCachePointCode(stationPoint.getPointCode());
            updateItem.setCacheWcsJobId(wcsRobotJobId);
        } else {
            updateItem.setWcsJobId(wcsRobotJobId);
        }

        if(Floor2PlcJobTaskType.getByName(task.getPlcJobType()) == Floor2PlcJobTaskType.PRODUCE &&
                (moveType == Floor2WcsPlcJobMoveType.DIRECT || moveType == Floor2WcsPlcJobMoveType.TAIL) &&
                StringUtils.isBlank(task.getTargetPointCode())) {
            updateItem.setTargetPointCode(stationPoint.getPointCode());
        }

        int taskUpdateRes = 0;
        synchronized (task.getBucketCode().intern()) {
            taskUpdateRes = floor2PlcJobTaskDas.update(updateItem, task.getStatus());
        }

        if (taskUpdateRes < 1) {
            LOGGER.warn("createWcsMoveTask[" + task.getId() + "] status: " + moveType.plcTaskStatus + ", moveType: " + moveType + ", update failed(row:0): task: " + ObjectMappers.mustWriteValue(task) +
                    ", point: " + ObjectMappers.mustWriteValue(stationPoint));
            return false;
        }

        Floor2StationPoint pointUpdate = new Floor2StationPoint();
        pointUpdate.setId(stationPoint.getId());
        pointUpdate.setStatus(Floor2PointOccupyStatus.OCCUPIED.name());
        floor2StationPointDas.update(pointUpdate);

        Floor2WcsMoveJob floor2WcsMoveJob = new Floor2WcsMoveJob();
        floor2WcsMoveJob.setStatus(Floor2WcsMoveJobStatus.WAIT_PUSH.name());
        floor2WcsMoveJob.setBucketCode(task.getBucketCode());
        floor2WcsMoveJob.setPlcTaskId(task.getId());
        floor2WcsMoveJob.setPointCode(stationPoint.getPointCode());
        floor2WcsMoveJob.setZoneCode(task.getZoneCode());
        floor2WcsMoveJob.setWarehouseCode(task.getWarehouseCode());
        floor2WcsMoveJob.setWcsJobId(wcsRobotJobId);
        floor2WcsMoveJob.setPlcJobType(moveType.name());
        floor2WcsMoveJobDas.insert(floor2WcsMoveJob);
        return true;
    }

    public void wcsRotJobMove() {
        List<Floor2WcsMoveJob> jobs = floor2WcsMoveJobDas.pageStatusJobs(new Page<>(0, DEFAULT_MOVE_TASK_COUNT), Floor2WcsMoveJobStatus.WAIT_PUSH.name(), Floor2WcsMoveJobStatus.PUSHED.name());
        Map<String, List<Floor2WcsMoveJob>> bucketMoveJobMap = new HashMap<>();
        for(Floor2WcsMoveJob job : jobs) {
            SetUtil.pick(bucketMoveJobMap, job.getBucketCode(), job);
        }

        for(Map.Entry<String, List<Floor2WcsMoveJob>> e : bucketMoveJobMap.entrySet()) {
            Floor2WcsMoveJob job = SetUtil.first(e.getValue());
            if(job != null && Floor2WcsMoveJobStatus.getByName(job.getStatus()) == Floor2WcsMoveJobStatus.WAIT_PUSH) {
                WcsStandardResponse wcsStandardResponse = floor2WcsStandardJob.robotMove(job.getBucketCode(), job.getPointCode(), job.getWcsJobId(),job.getWarehouseCode(), job.getZoneCode());
                if(wcsStandardResponse.ok()) {
                    Floor2WcsMoveJob updateItem = new Floor2WcsMoveJob();
                    updateItem.setId(job.getId());
                    updateItem.setStatus(Floor2WcsMoveJobStatus.PUSHED.name());
                    floor2WcsMoveJobDas.update(updateItem);
                }
            }
        }
    }
}
