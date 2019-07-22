package com.si.upstream.dal.entity.floor3;

import com.baomidou.mybatisplus.annotation.TableName;
import com.si.upstream.dal.entity.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sunxibin
 */
@Data
@Builder
@TableName(value = "inner_job")
@AllArgsConstructor
@NoArgsConstructor
public class InnerJobDO extends BaseDO implements Serializable {

    /**
     * 对应上游任务Id
     */
    private Long upstreamJobId;
    /**
     * 任务唯一ID inner-time(年月日时分秒毫秒)-自增状态码;
     */
    private String innerJobId;
    /**
     * 任务类型
     * [
     * move,
     * bucket_move
     * ]
     */
    private String type;
    /**
     * 任务是否放下货架标识、
     * [
     * online,
     * offline
     * ]
     */
    private String letDownFlag;
    /**
     * 任务完成后需不需要请求PLC设备开门
     * [
     * 0,  //不需要发送请求
     * 1   //需要发送请求
     * ]
     */
    private Boolean flag;
    /**
     * AGV进站请求的类型
     * [
     * 1,   //驶入申请
     * 2,   //驶入完成上报
     * 3,   //驶出申请
     * 4    //驶出完成上报
     * ]
     */
    private Integer requestType;
    /**
     * 需要发起上报的目标作业台:进站申请，任务完成
     */
    private String plcStationCode;
    /**
     * 任务状态
     */
    private String status;
    /**
     * 任务对应起始点位
     */
    private String sourcePoint;
    /**
     * 任务对应目标点位
     */
    private String targetPoint;
    /**
     * agv的目标点
     */
    private String agvEndPoint;
    /**
     * bucketMove的类型
     * [
     * 1,  //货架入场不校验货架的货架移位任务 ： startPoint, endPoint, online或者offline     //不传(bucketCode, agvEndPoint, agvCode)
     * 2,  //货架二段移位任务 ： bucketCode, endPoint, agvEndPoint, offline                 //不传(startPoint, agvCode)
     * 3,  //指定AGV的货架移位 ： agvCode, startPoint, endPoint, offline                    //不传(bucketCode，agvEndPoint)
     * 4， //普通货架移位 ：bucketCode, endPoint                                            //不传(agvCode, agvEndPoint, startPoint)
     * ]
     */
    private String bucketMoveType;
}
