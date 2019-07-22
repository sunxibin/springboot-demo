package com.si.upstream.model.gateway.request;

import lombok.Data;

/**
 * AGV/货架驶入驶出申请
 *
 * @author sunxibin
 */
@Data
public class InOutRequest {
    /**
     * 仓库编号
     */
    private String warehouseCode;
    /**
     * 库区编号
     */
    private String zoneCode;
    /**
     * 机械手作业台编号
     */
    private String stationCode;
    /**
     * 操作 [
     *       1,       // 驶入申请
     *       2,       // 驶入完成上报
     *       3,       // 驶出申请
     *       4        // 驶出完成上报
     *      ]
     */
    private int operation;
}
