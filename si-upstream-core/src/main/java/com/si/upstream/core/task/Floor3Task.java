package com.si.upstream.core.task;

import com.si.upstream.common.config.FloorConfiguration;
import com.si.upstream.common.enums.*;
import com.si.upstream.common.util.IdCreater;
import com.si.upstream.core.calculation.TaskCalculation;
import com.si.upstream.core.manager.Floor3TaskManager;
import com.si.upstream.dal.das.floor3.*;
import com.si.upstream.dal.entity.floor3.InnerJobDO;
import com.si.upstream.dal.entity.floor3.StationDO;
import com.si.upstream.dal.entity.floor3.UpstreamJobDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sunxibin
 */
@Slf4j
@Component
public class Floor3Task extends BaseTask {
    @Resource
    private UpstreamJobDas upstreamJobDas;
    @Resource
    private InnerJobDas innerJobDas;
    @Resource
    private InnerJobService innerJobService;
    @Resource
    private WayPointDas wayPointDas;
    @Resource
    private StationDas stationDas;
    @Resource
    private Floor3TaskManager taskManager;
    @Resource
    private RestTemplate restTemplate;


    @Value("${task.cron.floor3}")
    private String cron;

    @Override
    protected String getCron() {
        if (StringUtils.isBlank(cron)) {
        }
        return cron;
    }

    @Override
    protected boolean isOpen() {
        return true;
    }

    @Override
    protected void executeTask() {
        //获取所有初始化状态的上游任务，按照任务类型分成四类
        List<UpstreamJobDO> pendingProcessJobList = upstreamJobDas.queryByStatus(PlcJobStatus.INIT.name(), FloorConfiguration.floor3ZoneCode);
        Map<String, List<UpstreamJobDO>> groups = TaskCalculation.getPlcJobGroup(pendingProcessJobList);
        log.info("pending process jobList : {}", groups);

        //分级任务计算
        //1、二次补货去机械手的任务
        if (null != groups.get(PlcJobType.second_time_to_mechanical.name())) {
            List<UpstreamJobDO> list = groups.get(PlcJobType.second_time_to_mechanical.name());
            log.info("pending process second_time_to_mechanical jobList: {}", list);
            processSecondTimeToMechanical(list);
        }
        //2、二次补货离开机械手的任务
        if (null != groups.get(PlcJobType.second_time_away_mechanical.name())) {
            List<UpstreamJobDO> list = groups.get(PlcJobType.second_time_away_mechanical.name());
            log.info("pending process second_time_away_mechanical jobList : {}", list);
            processSecondTimeAwayMechanical(list);
        }
        //3、首次补货去机械手+去人工区的任务
        if (null != groups.get(PlcJobType.first_time_to_consumer.name())) {
            List<UpstreamJobDO> list = groups.get(PlcJobType.first_time_to_consumer.name());
            log.info("pending process first_time_to_consumer jobList : {}", list);
            processFirstTimeToConsumer(list);
        }
        //4、首次补货离开机械手+人工区的任务
        if (null != groups.get(PlcJobType.first_time_away_consumer.name())) {
            List<UpstreamJobDO> list = groups.get(PlcJobType.first_time_away_consumer.name());
            log.info("pending process first_time_away_consumer jobList : {}", list);
            processFirstTimeAwayConsumer(list);
        }
    }

    /**
     * 1、处理二次补货去机械手的任务： 目标点位一定是外侧点位
     */
    private void processSecondTimeToMechanical(List<UpstreamJobDO> list) {
        for (UpstreamJobDO jobDO : list) {
            //获取目标点位
            String targetPoint = null;
            if (null != (targetPoint = taskManager.getEndPointFromMechanicalStationForSecondOrder(jobDO.getEndStation()))) {
                //对任务的实际目标点位进行预占
                taskManager.updatePointState(targetPoint, PointState.Occupying);
                //获取机械手区域前一个点位
                String endPoint1 = taskManager.getPreviousPoint(jobDO.getEndStation());
                //1、不指定货架的货架移位任务+货架入场，AGV需要在机械手区域前一个点停下，申请进入
                InnerJobDO innerJob1 = createBucketMoveJob(jobDO, null, endPoint1, null, false,
                        true, AgvRequestOperationEnum.entry_application, jobDO.getEndStation(), BucketMoveType.without_bucket);
                //2、申请成功后才可以派发下一个任务，指定货架的二段移位任务
                InnerJobDO innerJob2 = createBucketMoveJob(jobDO, endPoint1, targetPoint, endPoint1, true,
                        true, AgvRequestOperationEnum.exit_completion, jobDO.getEndStation(), BucketMoveType.secondary_move);
                //任务入库，派发第一段任务
                List<InnerJobDO> innerJobList = new ArrayList<>();
                innerJobList.add(innerJob1);
                innerJobList.add(innerJob2);
                boolean isSuccess = innerJobService.saveBatch(innerJobList);
                if (isSuccess) {
                    //任务派发
                    boolean result = taskManager.deliveringJob(innerJob1, null, null);
                    handleResult(result, jobDO, targetPoint, innerJobList);
                }
            } else {
                log.info("can't find target point. upstreamJobId=" + jobDO.getId());
            }
        }
    }

    /**
     * 2、处理二次补货离开机械手的任务： 起始点位一定是外侧点位
     */
    private void processSecondTimeAwayMechanical(List<UpstreamJobDO> list) {
        for (UpstreamJobDO jobDO : list) {
            //判断出入场点是否有空闲点位，有则获取
            String targetPoint = null;
            if (null != (targetPoint = taskManager.getEndPointFromInOutArea(jobDO.getEndStation()))) {
                //对任务的实际目标点位进行预占
                taskManager.updatePointState(targetPoint, PointState.Occupying);
                //1、AGV空车移动到机械手前一个点位，申请进入工作站
                String startPoint1 = taskManager.getPreviousPoint(jobDO.getStartStation());
                InnerJobDO innerJob1 = createMoveJob(jobDO, startPoint1, true, AgvRequestOperationEnum.entry_application, jobDO.getStartStation());
                //2、申请成功后进入下一步，指定AGV的货架移位任务(在任务完成上报的时候携带agvCode),移动到机械手前一个点告知出站完成
                InnerJobDO innerJob2 = createBucketMoveJob(jobDO, null, startPoint1, null, false,
                        true, AgvRequestOperationEnum.exit_completion, jobDO.getStartStation(), BucketMoveType.with_agv);
                //3、agv从机械手区前一个点移动到出入场点
                InnerJobDO innerJob3 = createBucketMoveJob(jobDO, null, targetPoint, null, true,
                        false, null, jobDO.getStartStation(), BucketMoveType.normal);
                //任务入库，派发第一段任务
                List<InnerJobDO> innerJobList = new ArrayList<>();
                innerJobList.add(innerJob1);
                innerJobList.add(innerJob2);
                innerJobList.add(innerJob3);
                boolean isSuccess = innerJobService.saveBatch(innerJobList);
                if (isSuccess) {
                    //任务派发
                    boolean result = taskManager.deliveringJob(innerJob1, null, null);
                    handleResult(result, jobDO, targetPoint, innerJobList);
                }
            } else {
                log.info("can't find target point. upstreamJobId=" + jobDO.getId());
            }
        }
    }

    /**
     * 3、处理首次补货去机械手+人工区的任务
     */
    private void processFirstTimeToConsumer(List<UpstreamJobDO> list) {
        for (UpstreamJobDO jobDO : list) {
            //获取目标点位
            String targetPoint = null;
            if (null != (targetPoint = taskManager.getEndPointFromWorkStation(jobDO.getEndStation()))) {
                //对任务的实际目标点位进行预占
                taskManager.updatePointState(targetPoint, PointState.Occupying);
                //机械手区的任务拆分同1，人工区域的任务不需要拆分
                StationDO stationDO = stationDas.queryByStationCode(jobDO.getEndStation());
                if (StationType.MechanicalOperationArea.name().equals(stationDO.getStationType())) {
                    //获取机械手区域前一个点位
                    String endPoint1 = taskManager.getPreviousPoint(jobDO.getEndStation());
                    //1、不指定货架的货架移位任务+货架入场，AGV需要在机械手区域前一个点停下，申请进入
                    InnerJobDO innerJob1 = createBucketMoveJob(jobDO, null, endPoint1, null, false,
                            true, AgvRequestOperationEnum.entry_application, jobDO.getEndStation(), BucketMoveType.without_bucket);
                    //2、申请成功后才可以派发下一个任务，指定货架的二段移位任务
                    InnerJobDO innerJob2 = createBucketMoveJob(jobDO, endPoint1, targetPoint, endPoint1, true,
                            true, AgvRequestOperationEnum.exit_completion, jobDO.getEndStation(), BucketMoveType.secondary_move);
                    //任务入库，派发第一段任务
                    List<InnerJobDO> innerJobList = new ArrayList<>();
                    innerJobList.add(innerJob1);
                    innerJobList.add(innerJob2);
                    boolean isSuccess = innerJobService.saveBatch(innerJobList);
                    if (isSuccess) {
                        //任务派发
                        boolean result = taskManager.deliveringJob(innerJob1, null, null);
                        handleResult(result, jobDO, targetPoint, innerJobList);
                    }
                } else if (StationType.ManualOperationArea.name().equals(stationDO.getStationType())) {
                    InnerJobDO innerJobDO = createBucketMoveJob(jobDO, null, targetPoint, null, true,
                            false, null, jobDO.getEndStation(), BucketMoveType.without_bucket);
                    int impactRows = innerJobDas.insert(innerJobDO);
                    if (impactRows > 0) {
                        boolean result = taskManager.deliveringJob(innerJobDO, null, null);
                        List<InnerJobDO> innerJobList = new ArrayList<>();
                        innerJobList.add(innerJobDO);
                        handleResult(result, jobDO, targetPoint, innerJobList);
                    }
                }
            } else {
                log.info("can't find target point. upstreamJobId=" + jobDO.getId());
            }
        }
    }

    /**
     * 4、处理首次补货离开机械手+人工区的任务
     */
    private void processFirstTimeAwayConsumer(List<UpstreamJobDO> list) {
        for (UpstreamJobDO jobDO : list) {
            //判断去出入场点的任务能否下发
            if (taskManager.isExecutableToProducer(jobDO.getStartPoint(), jobDO.getStartStation())) {
                //判断出入场点是否有空闲点位，有则获取
                String targetPoint = null;
                if (null != (targetPoint = taskManager.getEndPointFromInOutArea(jobDO.getEndStation()))) {
                    //对任务的实际目标点位进行预占
                    taskManager.updatePointState(targetPoint, PointState.Occupying);
                    //机械手区域任务拆分同2，人工区域直接下发搬运任务
                    StationDO stationDO = stationDas.queryByStationCode(jobDO.getStartStation());
                    if (StationType.MechanicalOperationArea.name().equals(stationDO.getStationType())) {
                        //1、AGV空车移动到机械手前一个点位，申请进入工作站
                        String startPoint1 = taskManager.getPreviousPoint(jobDO.getStartStation());
                        InnerJobDO innerJob1 = createMoveJob(jobDO, startPoint1, true, AgvRequestOperationEnum.entry_application, jobDO.getStartStation());
                        //2、申请成功后进入下一步，指定AGV的货架移位任务(在任务完成上报的时候携带agvCode)
                        InnerJobDO innerJob2 = createBucketMoveJob(jobDO, null, startPoint1, null, false,
                                true, AgvRequestOperationEnum.exit_completion, jobDO.getStartStation(), BucketMoveType.with_agv);
                        //3、从机械手区域前一个点移动到出入场点
                        InnerJobDO innerJob3 = createBucketMoveJob(jobDO, null, targetPoint, null, true,
                                false, null, jobDO.getStartStation(), BucketMoveType.normal);
                        //任务入库，派发第一段任务
                        List<InnerJobDO> innerJobList = new ArrayList<>();
                        innerJobList.add(innerJob1);
                        innerJobList.add(innerJob2);
                        innerJobList.add(innerJob3);
                        boolean isSuccess = innerJobService.saveBatch(innerJobList);
                        if (isSuccess) {
                            //任务派发
                            boolean result = taskManager.deliveringJob(innerJob1, null, null);
                            handleResult(result, jobDO, targetPoint, innerJobList);
                        }
                    } else if (StationType.ManualOperationArea.name().equals(stationDO.getStationType())) {
                        InnerJobDO innerJobDO = createBucketMoveJob(jobDO, null, targetPoint, null, true,
                                false, null, jobDO.getStartStation(), BucketMoveType.without_bucket);
                        int impactRows = innerJobDas.insert(innerJobDO);
                        if (impactRows > 0) {
                            //任务派发
                            boolean result = taskManager.deliveringJob(innerJobDO, null, null);
                            List<InnerJobDO> innerJobList = new ArrayList<>();
                            innerJobList.add(innerJobDO);
                            handleResult(result, jobDO, targetPoint, innerJobList);
                        }
                    }
                } else {
                    log.info("can't find target point. upstreamJobId=" + jobDO.getId());
                }
            } else {
                log.info("current task cannot be issued. upstreamJobId=" + jobDO.getId());
            }
        }
    }

    /**
     * 创建货架移位任务
     *
     * @param jobDO       上游任务
     * @param startPoint  任务起始点，可以为null
     * @param endPoint    任务目标点，可以为null
     * @param agvEndPoint AGV停靠目标点，可以为null
     * @param letDownFlag 是否放下货架
     * @param flag        任务完成后是否需要发进站请求
     * @param operation   进站请求的类型
     * @param stationCode 需要发起进站请求的目标作业台
     * @return
     */
    private InnerJobDO createBucketMoveJob(UpstreamJobDO jobDO, String startPoint, String endPoint, String agvEndPoint, boolean letDownFlag,
                                           boolean flag, AgvRequestOperationEnum operation, String stationCode, BucketMoveType bucketMoveType) {
        InnerJobDO innerJobDO = InnerJobDO.builder()
                .upstreamJobId(jobDO.getId())
                .innerJobId(IdCreater.getInnerJobId())
                .type(InnerJobType.BUCKET_MOVE.name())
                .letDownFlag((true == letDownFlag) ? LetDownFlag.offline.name() : LetDownFlag.online.name())
                .flag(flag)
                .plcStationCode(stationCode)
                .status(InnerJobStatus.INIT.name())
                .sourcePoint((null != startPoint) ? startPoint : jobDO.getStartPoint())
                .targetPoint((null != endPoint) ? endPoint : jobDO.getEndPoint())
                .agvEndPoint(agvEndPoint)
                .bucketMoveType(bucketMoveType.name())
                .build();
        innerJobDO.setWarehouseCode(jobDO.getWarehouseCode());
        innerJobDO.setZoneCode(jobDO.getZoneCode());
        if (flag) {
            innerJobDO.setRequestType(operation.getCode());
        }
        return innerJobDO;
    }

    /**
     * 创建空车移位任务
     *
     * @param endPoint    任务目标点，不可以为null
     * @param flag        任务完成后是否需要发进站请求
     * @param stationCode 需要发起进站请求的目标作业台
     * @return 目标任务
     */
    private InnerJobDO createMoveJob(UpstreamJobDO jobDO, String endPoint, boolean flag, AgvRequestOperationEnum operation, String stationCode) {
        InnerJobDO innerJobDO = InnerJobDO.builder()
                .upstreamJobId(jobDO.getId())
                .innerJobId(IdCreater.getInnerJobId())
                .type(InnerJobType.MOVE.name())
                .flag(flag)
                .plcStationCode(stationCode)
                .status(InnerJobStatus.INIT.name())
                .targetPoint(endPoint)
                .build();
        innerJobDO.setWarehouseCode(jobDO.getWarehouseCode());
        innerJobDO.setZoneCode(jobDO.getZoneCode());
        if (flag) {
            innerJobDO.setRequestType(operation.getCode());
        }
        return innerJobDO;
    }

    /**
     * 处理任务下发结果
     * 如果下发失败，需要处理 --> 释放点位，回滚任务
     *
     * @param result       任务下发的结果
     * @param jobDO        PLC任务
     * @param targetPoint  任务的目标点位
     * @param innerJobList 内部任务组
     */
    private void handleResult(boolean result, UpstreamJobDO jobDO, String targetPoint, List<InnerJobDO> innerJobList) {
        if (result) {
            //如果任务下发成功，修改任务状态
            taskManager.updateUpstreamJobStatus(jobDO.getId(), PlcJobStatus.EXECUTING.name(), jobDO.getStartPoint(), targetPoint);
            taskManager.updateInnerJobStatus(innerJobList.get(0).getInnerJobId(), InnerJobStatus.SEND.name());
            taskManager.updatePointState(jobDO.getStartPoint(), PointState.Occupying);

            log.info("dispatch success. upstreamJob = {}", jobDO);
        } else {
            //如果任务下发失败，内部任务标记为失败，同时释放目标点位
            taskManager.updatePointState(targetPoint, PointState.Free);
            for (InnerJobDO innerJob : innerJobList) {
//                taskManager.updateInnerJobStatus(innerJobList.get(0).getInnerJobId(), InnerJobStatus.FAILURE.name());
                if (null != innerJob.getId()) {
                    innerJobDas.delete(innerJob.getId());
                }
            }
            log.info("dispatch failure. upstreamJob = {}", jobDO);
        }
    }

}
