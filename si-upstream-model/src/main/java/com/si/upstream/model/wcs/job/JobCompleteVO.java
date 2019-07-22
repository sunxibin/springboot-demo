package com.si.upstream.model.wcs.job;

import lombok.Data;

/**
 * @author sunxibin
 */
@Data
public class JobCompleteVO {
//    private String warehouseId;
//    private String zoneCode;
//    private String source;
    private String robotJobId;
    private String bucketCode;
    private String agvCode;
    private String endPoint;
    private String endArea;
    private String state;
}
