package com.si.upstream.model.wcs.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 空车调度任务，只有一个起始点
 * @author sunxibin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoveJobVO {

    private String agvCode;
    private String endArea;
    private String endPoint;
    private String robotJobId;
    private String startPoint;
    private Long warehouseId;
    private String zoneCode;
    /**
     * 任务来源：['3F','2F']
     */
    private String source;
}
