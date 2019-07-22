package com.si.upstream.model.wcs.job;

import lombok.Data;

/**
 * @author sunxibin
 */
@Data
public class BucketOutRequest {
    /**
     * 仓库编号
     */
    private String warehouseId;
    /**
     * 库区编号
     */
    private String zoneCode;
    /**
     * 任务编号
     */
    private String robotJobId;
    /**
     * 出场点位
     */
    private String pointCode;
}
