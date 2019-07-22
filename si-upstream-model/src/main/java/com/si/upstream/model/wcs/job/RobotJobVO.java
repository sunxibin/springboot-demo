package com.si.upstream.model.wcs.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 货架移动任务
 * 1、指定起始点
 * 2、指定起始点、AGV
 * 3、指定起始点、AGV、货架
 *
 * @author sunxibin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RobotJobVO {

    private Long warehouseId;
    private String zoneCode;
    private String robotJobId;
    /**
     * 0
     */
    private Integer checkCode;
    /**
     * ['2F','3F']
     */
    private String source;
    private String bucketCode;
    /**
     * ['online','offline']
     */
    private String letDownFlag;
    /**
     * 0
     */
    private Integer workFace;
    private String agvCode;
    private String startPoint;
    private String endPoint;
    private String agvEndPoint;
    /**
     * ['BUCKET']
     */
    private String transportEntityType;

}
