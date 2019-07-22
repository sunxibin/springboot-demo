package com.si.upstream.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.si.upstream.common.enums.*;
import com.si.upstream.common.util.UrlUtils;
import com.si.upstream.dal.das.floor3.InnerJobDas;
import com.si.upstream.dal.das.floor3.StationDas;
import com.si.upstream.dal.das.floor3.UpstreamJobDas;
import com.si.upstream.dal.das.floor3.WayPointDas;
import com.si.upstream.dal.entity.floor3.InnerJobDO;
import com.si.upstream.dal.entity.floor3.StationDO;
import com.si.upstream.dal.entity.floor3.UpstreamJobDO;
import com.si.upstream.dal.entity.floor3.WayPointDO;
import com.si.upstream.model.wcs.job.MoveJobVO;
import com.si.upstream.model.wcs.job.RobotJobVO;
import com.si.upstream.model.wcs.result.SimpleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.print.attribute.standard.JobState;
import java.util.*;

/**
 * @author sunxibin
 */
@Slf4j
@Service
public class Floor3TaskManager {
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


    /**
     * 判断目标点位是否空闲
     *
     * @param pointCode
     * @return true 表示空闲
     */
    public boolean isFreePoint(String pointCode) {
        WayPointDO point = wayPointDas.queryByPointCode(pointCode);
        if (PointState.Free.getCode() == point.getOccupiedState()) {
            return true;
        }
        return false;
    }

    /**
     * 判断去作业区的任务能不能下发
     *
     * @param pointCode 任务的目标点
     * @param station   任务的目标工作站
     * @return
     */
    public boolean isExecutableToConsumer(String pointCode, String station) {
        WayPointDO point = wayPointDas.queryByPointCode(pointCode);
        //如果是内侧点位，且内侧点位空闲 --> true
        //如果是外侧点位，且内侧点位已经被占用 --> true
        if (PointType.InsidePoint.getCode() == point.getPointType()
                && PointState.Free.getCode() == point.getOccupiedState()) {
            return true;
        } else if (PointType.OutsidePoint.getCode() == point.getPointType()) {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("station", station);
            columnMap.put("point_type", PointType.InsidePoint.getCode());
            List<WayPointDO> pointDOList = wayPointDas.queryByCondition(columnMap);
            if (!CollectionUtils.isEmpty(pointDOList)) {
                WayPointDO insidePoint = pointDOList.get(0);
                if (PointState.Occupied.getCode() == insidePoint.getOccupiedState()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断去出入场点的任务能否下发
     *
     * @param pointCode 任务的起始点
     * @param station   任务起始点的区域
     * @return
     */
    public boolean isExecutableToProducer(String pointCode, String station) {
        WayPointDO point = wayPointDas.queryByPointCode(pointCode);
        //如果是外侧点位 --> true
        //如果是内侧点位，且外侧点位空闲 --> true
        //TODO 如果外侧点位没有货架怎么办，要不要直接将任务失败
        if (PointType.OutsidePoint.getCode() == point.getPointType()) {
            return true;
        } else if (PointType.InsidePoint.getCode() == point.getPointType()) {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("station", station);
            columnMap.put("point_type", PointType.OutsidePoint.getCode());
            List<WayPointDO> pointDOList = wayPointDas.queryByCondition(columnMap);
            if (!CollectionUtils.isEmpty(pointDOList)) {
                WayPointDO outsidePoint = pointDOList.get(0);
                if (PointState.Free.getCode() == outsidePoint.getOccupiedState()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 占用出入场点的一个点位
     *
     * @return 占用成功则返回点位的pointCode, 占用失败则返回null
     */
    public String occupyingInOutArea() {
        //获取出入场点集合
        Map<String, Object> columnMap1 = new HashMap<>();
        columnMap1.put("station_type", StationType.InOutArea.name());
        List<StationDO> stationDOList = stationDas.queryByCondition(columnMap1);
        List<String> codeList = null;
        if (!CollectionUtils.isEmpty(stationDOList)) {
            codeList = new ArrayList<>();
            for (StationDO stationDO : stationDOList) {
                codeList.add(stationDO.getStationCode());
            }
        }

        if (null != codeList) {
            List<WayPointDO> pointList = wayPointDas.queryByStations(codeList);
            if (!CollectionUtils.isEmpty(pointList)) {
                for (WayPointDO pointDO : pointList) {
                    if (PointState.Free.getCode() == pointDO.getOccupiedState()) {
                        //占用点位需要先修改状态
                        WayPointDO newPoint = new WayPointDO();
                        newPoint.setPointCode(pointDO.getPointCode());
                        newPoint.setOccupiedState(PointState.Occupying.getCode());
                        int impactRows = wayPointDas.update(newPoint);
                        if (impactRows > 0) {
                            return pointDO.getPointCode();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从工作站获取一个可用点位，符合以下条件的点位可用
     * 1.内侧点位空闲，则返回内侧点位
     * 2.如果内侧点位不空闲，且无任务，并且外侧点位空闲，则返回外侧点位
     *
     * @param stationCode 工作站编码
     * @return 可用的endPoint
     */
    public String getEndPointFromWorkStation(String stationCode) {
        String pointCode = null;
        StationDO stationDO = stationDas.queryByStationCode(stationCode);
        if (null != stationDO && (
                StationType.MechanicalOperationArea.name().equals(stationDO.getStationType())
                        || StationType.ManualOperationArea.name().equals(stationDO.getStationType())
        )) {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("station", stationCode);
            List<WayPointDO> pointList = wayPointDas.queryByCondition(columnMap);
            if (!CollectionUtils.isEmpty(pointList)) {
                WayPointDO insidePoint = pointList.stream().filter(point -> PointType.InsidePoint.getCode() == point.getPointType()).findFirst().orElse(null);
                WayPointDO outsidePoint = pointList.stream().filter(point -> PointType.OutsidePoint.getCode() == point.getPointType()).findFirst().orElse(null);
                if (null != insidePoint && PointState.Free.getCode() == insidePoint.getOccupiedState()) {
                    pointCode = insidePoint.getPointCode();
                } else if (null != insidePoint && null != outsidePoint
                        && PointState.Occupied.getCode() == insidePoint.getOccupiedState()
                        && PointState.Free.getCode() == outsidePoint.getOccupiedState()) {
                    //查询从该工作站到出入场点的 初始化状态的 任务集合
                    List<UpstreamJobDO> insidePointJob = upstreamJobDas.queryInitJobBySourcePoint(insidePoint.getPointCode(), PlcJobStatus.INIT.name());
                    if (CollectionUtils.isEmpty(insidePointJob)) {
                        pointCode = outsidePoint.getPointCode();
                    } else {
                        log.info("this station's inside point has a init upstreamJob. stationCode=" + stationCode);
                    }
                }
            }
        }
        return pointCode;
    }

    /**
     * 获取二次补货去机械手区域的目标点位
     *
     * @param stationCode 机械区域的工作站
     * @return 可用点位（一定要是外侧点位）
     */
    public String getEndPointFromMechanicalStationForSecondOrder(String stationCode) {
        String pointCode = null;
        StationDO stationDO = stationDas.queryByStationCode(stationCode);
        if (null != stationDO && StationType.MechanicalOperationArea.name().equals(stationDO.getStationType())) {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("station", stationCode);
            List<WayPointDO> pointList = wayPointDas.queryByCondition(columnMap);
            if (!CollectionUtils.isEmpty(pointList)) {
                WayPointDO insidePoint = pointList.stream().filter(point -> PointType.InsidePoint.getCode() == point.getPointType()).findFirst().orElse(null);
                WayPointDO outsidePoint = pointList.stream().filter(point -> PointType.OutsidePoint.getCode() == point.getPointType()).findFirst().orElse(null);
                if (null != insidePoint && null != outsidePoint
                        && PointState.Occupied.getCode() == insidePoint.getOccupiedState()
                        && PointState.Free.getCode() == outsidePoint.getOccupiedState()) {
                    pointCode = outsidePoint.getPointCode();
                }
            }
        }
        return pointCode;
    }

    /**
     * 从指定出入场区域获取一个可用点位
     *
     * @param stationCode 出入场区域的编码
     * @return 可用点位的pointCode
     */
    public String getEndPointFromInOutArea(String stationCode) {
        StationDO stationDO = stationDas.queryByStationCode(stationCode);
        if (null != stationDO && StationType.InOutArea.name().equals(stationDO.getStationType())) {
            //查询以该出入场区域为sourceStation的init状态的任务
            List<UpstreamJobDO> stationJobList = upstreamJobDas.queryInitJobBySourceStation(stationCode, PlcJobStatus.INIT.name());
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("station", stationCode);
            columnMap.put("occupied_state", PointState.Free.getCode());
            List<WayPointDO> pointList = wayPointDas.queryByCondition(columnMap);
            if (!CollectionUtils.isEmpty(pointList)) {
                //排除已经被占用的出入场点
                if (!CollectionUtils.isEmpty(stationJobList)) {
                    Iterator<WayPointDO> iterator = pointList.iterator();
                    while (iterator.hasNext()) {
                        WayPointDO point = iterator.next();
                        stationJobList.forEach(job -> {
                            if (point.getPointCode().equalsIgnoreCase(job.getStartPoint())) {
                                iterator.remove();
                            }
                        });
                    }
                }
                WayPointDO pointDO = pointList.get(0);
                return pointDO.getPointCode();
            }
        }
        return null;
    }

    /**
     * 占用一个点位
     *
     * @param pointCode 目标点位
     * @return 占用成功返回true
     */
    public boolean occupyingPoint(String pointCode) {
        WayPointDO point = new WayPointDO();
        point.setPointCode(pointCode);
        point.setOccupiedState(PointState.Occupying.getCode());
        int impactRows = wayPointDas.update(point);
        if (impactRows > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取机械手区域的前一个点
     *
     * @param areaCode 指定的机械手区域
     * @return 目标点位编码
     */
    public String getPreviousPoint(String areaCode) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("station", areaCode);
        columnMap.put("point_type", PointType.PreviousPoint.getCode());
        List<WayPointDO> pointList = wayPointDas.queryByCondition(columnMap);
        if (!CollectionUtils.isEmpty(pointList)) {
            WayPointDO pointDO = pointList.get(0);
            return pointDO.getPointCode();
        }
        return null;
    }

    /**
     * 修改点位的状态
     */
    public boolean updatePointState(String pointCode, PointState state) {
        WayPointDO pointCarrier = new WayPointDO();
        pointCarrier.setPointCode(pointCode);
        pointCarrier.setOccupiedState(state.getCode());
        int impactRows = wayPointDas.update(pointCarrier);
        if (impactRows > 0) {
            return true;
        }
        return false;
    }

    /**
     * 修改InnerJob的状态
     */
    public boolean updateInnerJobStatus(String jobId, String status) {
        InnerJobDO innerJob = new InnerJobDO();
        innerJob.setInnerJobId(jobId);
        innerJob.setStatus(status);
        int impactRows = innerJobDas.update(innerJob);
        if (impactRows > 0) {
            return true;
        }
        return false;
    }

    /**
     * 修改UpstreamJob的状态,以及任务过程中货架实际占用的起始点和目标点
     */
    public boolean updateUpstreamJobStatus(Long jobId, String status, String realStartPoint, String realEndPoint) {
        UpstreamJobDO upstreamJob = new UpstreamJobDO();
        upstreamJob.setId(jobId);
        upstreamJob.setStatus(status);
        upstreamJob.setRealStartPoint(realStartPoint);
        upstreamJob.setRealEndPoint(realEndPoint);
        int impactRows = upstreamJobDas.update(upstreamJob);
        if (impactRows > 0) {
            return true;
        }
        return false;
    }

    /**
     * 将InnerJobDO转换成RobotJobVO或者MoveJobVO
     */
    public Object convertWcsJob(InnerJobDO innerJob, String bucketCode, String agvCode) {
        if (InnerJobType.BUCKET_MOVE.name().equals(innerJob.getType())) {
            //创建RobotJob
            RobotJobVO robotJob = RobotJobVO.builder()
                    .warehouseId(Long.valueOf(innerJob.getWarehouseCode()))
                    .zoneCode(innerJob.getZoneCode())
                    .robotJobId(innerJob.getInnerJobId())
                    .checkCode(0)
                    .source(FloorEnum.Floor3.getFloorId())
                    .bucketCode(bucketCode)
                    .letDownFlag(innerJob.getLetDownFlag())
                    .workFace(0)
                    .agvCode(agvCode)
                    .startPoint(innerJob.getSourcePoint())
                    .endPoint(innerJob.getTargetPoint())
                    .agvEndPoint(innerJob.getAgvEndPoint())
                    .transportEntityType("BUCKET")
                    .build();
            return robotJob;
        } else {
            //创建MoveJob
            MoveJobVO moveJob = MoveJobVO.builder()
                    .warehouseId(Long.valueOf(innerJob.getWarehouseCode()))
                    .zoneCode(innerJob.getZoneCode())
                    .robotJobId(innerJob.getInnerJobId())
                    .source(FloorEnum.Floor3.getFloorId())
                    .agvCode(agvCode)
                    .startPoint(innerJob.getSourcePoint())
                    .endArea(null)
                    .endPoint(innerJob.getTargetPoint())
                    .build();
            return moveJob;
        }
    }

    /**
     * 根据任务获取对应的wcsUrl
     */
    public String getWcsUrl(InnerJobDO innerJob) {
        return (InnerJobType.BUCKET_MOVE.name().equals(innerJob.getType()))
                ? UrlUtils.getRobotJobUrl()
                : UrlUtils.getMoveJobUrl();
    }

    /**
     * 任务下发wcs
     *
     * @param innerJob
     * @return 任务下发结果
     */
    public boolean deliveringJob(InnerJobDO innerJob, String bucketCode, String agvCode) {
        Object wcsJob = convertWcsJob(innerJob, bucketCode, agvCode);
        parameterFiltering(wcsJob, innerJob.getBucketMoveType());
        String url = getWcsUrl(innerJob);

        HttpEntity<String> request = convertHttpEntity(wcsJob);
        try {
            SimpleResult result = restTemplate.postForObject(url, request, SimpleResult.class);
            return result.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("http error : {}", e.getMessage());
        }
        return false;
    }

    /**
     * 任务下发前需要根据任务类型过滤参数
     */
    private void parameterFiltering(Object o, String type) {
        if (o instanceof RobotJobVO) {
            if (BucketMoveType.without_bucket.name().equals(type)) {
                ((RobotJobVO) o).setBucketCode(null);
                ((RobotJobVO) o).setAgvEndPoint(null);
                ((RobotJobVO) o).setAgvCode(null);
            } else if (BucketMoveType.secondary_move.name().equals(type)) {
                ((RobotJobVO) o).setStartPoint(null);
                ((RobotJobVO) o).setAgvCode(null);
            } else if (BucketMoveType.with_agv.name().equals(type)) {
                ((RobotJobVO) o).setBucketCode(null);
                ((RobotJobVO) o).setAgvEndPoint(null);
            } else if (BucketMoveType.normal.name().equals(type)) {
                ((RobotJobVO) o).setStartPoint(null);
                ((RobotJobVO) o).setAgvEndPoint(null);
                ((RobotJobVO) o).setAgvCode(null);
            }
        }
    }

    public HttpEntity<String> convertHttpEntity(Object o) {
        String json = JSONObject.toJSONString(o);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(json, header);
        return request;
    }
}
