package com.si.upstream.model.wcs.job;

import lombok.Data;

/**
 * @author sunxibin
 */
@Data
public class AgvGoHomeRequest {
    private String warehouseCode;
    private String zoneCode;
}
