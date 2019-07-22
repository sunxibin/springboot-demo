package com.si.upstream.dal.entity.floor3;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName(value = "upstream_job")
@AllArgsConstructor
@NoArgsConstructor
public class UpstreamJobDO extends BaseDO implements Serializable {

    /**
     * 任务类型
     */
    private String jobType;
    /**
     * 任务的次序[
     *              0,  //第一次叫料
     *              1   //第二次叫料，优先级最高
     *           ]
     */
    private Integer orderTime;
    /**
     * 任务起始点
     */
    private String startPoint;
    /**
     * 起始点位所在的区域
     */
    private String startStation;
    /**
     * 任务目标点
     */
    private String endPoint;
    /**
     * 任务目标点所在的区域
     */
    private String endStation;
    /**
     * 任务状态
     */
    private String status;
    /**
     * 实际货架搬运任务的起始点
     */
    private String realStartPoint;
    /**
     * 实际货架搬运任务的终点
     */
    private String realEndPoint;
    /**
     * 该组下一个任务的ID : 默认null
     */
    private Long nextJobId;
}
