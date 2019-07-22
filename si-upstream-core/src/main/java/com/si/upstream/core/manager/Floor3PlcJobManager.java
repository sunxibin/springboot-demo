package com.si.upstream.core.manager;

import com.si.upstream.common.enums.*;
import com.si.upstream.common.util.IdCreater;
import com.si.upstream.common.util.UrlUtils;
import com.si.upstream.dal.das.floor3.InnerJobDas;
import com.si.upstream.dal.das.floor3.StationDas;
import com.si.upstream.dal.das.floor3.UpstreamJobDas;
import com.si.upstream.dal.das.floor3.WayPointDas;
import com.si.upstream.dal.entity.floor3.StationDO;
import com.si.upstream.dal.entity.floor3.UpstreamJobDO;
import com.si.upstream.dal.entity.floor3.WayPointDO;
import com.si.upstream.model.plc.PlcFrameData;
import com.si.upstream.model.plc.PlcJobMessage;
import com.si.upstream.model.wcs.job.AgvGoHomeRequest;
import com.si.upstream.model.wcs.job.BucketOutRequest;
import com.si.upstream.model.wcs.result.SimpleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author sunxibin
 */
@Slf4j
@Service
public class Floor3PlcJobManager {
    @Resource
    private UpstreamJobDas upstreamJobDas;
    @Resource
    private InnerJobDas innerJobDas;
    @Resource
    private WayPointDas wayPointDas;
    @Resource
    private StationDas stationDas;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private Floor3TaskManager floor3TaskManager;


    public PlcJobMessage hand(PlcFrameData<PlcJobMessage> plcFrameData) {
        boolean success = false;
        int type = plcFrameData.getType();
        PlcJobMessage message = (PlcJobMessage) plcFrameData.getBody();
        switch (type) {
            //炒料区到灌料区任务下发接口
            case 1:
                log.info("receive plc message. type=ConsumerToProducer. message:{}", message);
                success = handlerForConsumerToProducer(message);
                break;
            //灌料区到炒料区任务下发接口
            case 2:
                log.info("receive plc message. type=ProducerToConsumer. message:{}", message);
                success = handlerForProducerToConsumer(message);
                break;
            //货架出场接口
            case 4:
                log.info("receive plc message. type=BucketOut. message:{}", message);
                success = handlerForBucketOut(message);
                break;
            //AGV一键归巢接口
            case 6:
                log.info("receive plc message. type=AgvHoming. message:{}", message);
                success = handlerForAgvHoming(message);
                break;
            //任务取消接口
            case 7:
                log.info("receive plc message. type=JobCancel. message:{}", message);
                success = handlerForJobCancel(message);
                break;
            //do nothing
            default:
                break;
        }
        return PlcJobMessage.result(plcFrameData, success);
    }

    /**
     * 1、炒料区到灌料区任务下发
     */
    private boolean handlerForConsumerToProducer(PlcJobMessage message) {
        UpstreamJobDO upstreamJobDO = createUpstreamJob(message);
        if (legalityCheck(upstreamJobDO) && defineJobType(upstreamJobDO)) {
            Integer impactRows = upstreamJobDas.insert(upstreamJobDO);
            if (impactRows > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 2、灌料区到炒料区任务下发
     */
    private boolean handlerForProducerToConsumer(PlcJobMessage message) {
        UpstreamJobDO upstreamJobDO = createUpstreamJob(message);
        if (legalityCheck(upstreamJobDO) && defineJobType(upstreamJobDO)) {
            Integer impactRows = upstreamJobDas.insert(upstreamJobDO);
            if (impactRows > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 4、货架出场
     */
    private boolean handlerForBucketOut(PlcJobMessage message) {
        boolean flag = false;
        if (!StringUtils.isEmpty(message.getPointCode())) {
            WayPointDO point = wayPointDas.queryByPointCode(message.getPointCode());
            if (null != point && PointState.Occupied.getCode() == point.getOccupiedState()) {
                BucketOutRequest request = new BucketOutRequest();
                request.setWarehouseId(message.getWarehouseCode());
                request.setZoneCode(message.getZoneCode());
                request.setRobotJobId(IdCreater.getInnerJobId());
                request.setPointCode(message.getPointCode());

                String url = UrlUtils.getBucketOutUrl();
                HttpEntity<String> httpEntity = floor3TaskManager.convertHttpEntity(request);
                try {
                    SimpleResult result = restTemplate.postForObject(url, httpEntity, SimpleResult.class);
                    if (result.isSuccess()) {
                        wayPointDas.updateStateByPointCode(message.getPointCode(), PointState.Free.getCode());
                        flag = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("http error : {}", e.getMessage());
                }
            }
        }

        return flag;
    }

    /**
     * 6、AGV一键归巢
     */
    private boolean handlerForAgvHoming(PlcJobMessage message) {
        AgvGoHomeRequest request = new AgvGoHomeRequest();
        request.setWarehouseCode(message.getWarehouseCode());
        request.setZoneCode(message.getZoneCode());

        String url = UrlUtils.getAgvHomeUrl();
        HttpEntity<String> httpEntity = floor3TaskManager.convertHttpEntity(request);
        try {
            SimpleResult result = restTemplate.postForObject(url, httpEntity, SimpleResult.class);
            if (result.isSuccess()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 7、任务取消
     */
    private boolean handlerForJobCancel(PlcJobMessage message) {
        boolean flag = false;
        Integer impactRows = upstreamJobDas.cancelJob(message.getSourcePointCode(), message.getSourceStationCode(),
                PlcJobStatus.INIT.name(), PlcJobStatus.CANCEL.name());
        if (impactRows > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 确定任务的类型
     */
    private boolean defineJobType(UpstreamJobDO upstreamJobDO) {
        //任务类型定义结果标识，false标识结果定义失败
        boolean flag = false;

        StationDO startStation = stationDas.queryByStationCode(upstreamJobDO.getStartStation());
        WayPointDO startPoint = wayPointDas.queryByPointCode(upstreamJobDO.getStartPoint());
        StationDO endStation = stationDas.queryByStationCode(upstreamJobDO.getEndStation());

        String endStationType = endStation.getStationType();
        int orderTime = upstreamJobDO.getOrderTime();
        //根据目标区域，任务的order 确定任务的类型
        //二次补货去机械手区域
        if (PlcJobOrder.second.getCode() == orderTime && StationType.MechanicalOperationArea.name().equals(endStationType)) {
            upstreamJobDO.setJobType(PlcJobType.second_time_to_mechanical.name());
            flag = true;

        //二次补货离开机械手区域
        } else if (PlcJobOrder.second.getCode() == orderTime && StationType.InOutArea.name().equals(endStationType)) {
            upstreamJobDO.setJobType(PlcJobType.second_time_away_mechanical.name());
            //条件：起始点为机械手区 + 外侧点位
            if (PointType.OutsidePoint.getCode() == startPoint.getPointType()
                    && StationType.MechanicalOperationArea.name().equalsIgnoreCase(startStation.getStationType())) {
                flag = true;
            }

        //首次补货去机械手+人工区
        } else if (PlcJobOrder.first.getCode() == orderTime &&
                (StationType.MechanicalOperationArea.name().equals(endStationType) || StationType.ManualOperationArea.name().equals(endStationType))) {
            upstreamJobDO.setJobType(PlcJobType.first_time_to_consumer.name());
            flag = true;

        //首次补货离开机械手+人工区
        } else if (PlcJobOrder.first.getCode() == orderTime && StationType.InOutArea.name().equals(endStationType)) {
            upstreamJobDO.setJobType(PlcJobType.first_time_away_consumer.name());
            flag = true;
        }

        return flag;
    }

    /**
     * 任务合法性校验，检查点位是否存在
     */
    private boolean legalityCheck(UpstreamJobDO jobDO) {
        boolean flag = true;
        //检查相关点位和工作站是否存在
        WayPointDO startPoint = wayPointDas.queryByPointCode(jobDO.getStartPoint());
        StationDO startStation = stationDas.queryByStationCode(jobDO.getStartStation());
        StationDO endStation = stationDas.queryByStationCode(jobDO.getEndStation());
        if (flag && !StringUtils.isEmpty(jobDO.getStartPoint())) {
            if (null == startPoint) {
                log.info("plc message illegal. startPoint is null. UpstreamJobDO:{}", jobDO);
                flag = false;
            }
        }
        if (flag && !StringUtils.isEmpty(jobDO.getStartStation())) {
            //校验起始点和起始区域是否为同一个区域
            if (null == startStation || !startPoint.getStation().equalsIgnoreCase(startStation.getStationCode())) {
                log.info("plc message illegal. startStation is null or startPoint is not in startStation. UpstreamJobDO:{}", jobDO);
                flag = false;
            }
        }
        if (flag && !StringUtils.isEmpty(jobDO.getEndStation())) {
            if (null == endStation) {
                log.info("plc message illegal. endStation is null. UpstreamJobDO:{}", jobDO);
                flag = false;
            }
        }

        return flag;
    }

    /**
     * 创建UpstreamJobDO
     *
     * @param message 上游任务信息
     * @return
     */
    public UpstreamJobDO createUpstreamJob(PlcJobMessage message) {
        UpstreamJobDO upstreamJobDO = UpstreamJobDO.builder()
                .startStation(message.getSourceStationCode())
                .startPoint(message.getSourcePointCode())
                .endStation(message.getTargetStationCode())
                .orderTime(message.getOrder())
                .status(PlcJobStatus.INIT.name())
                .build();
        upstreamJobDO.setWarehouseCode(message.getWarehouseCode());
        upstreamJobDO.setZoneCode(message.getZoneCode());
        return upstreamJobDO;
    }

}
