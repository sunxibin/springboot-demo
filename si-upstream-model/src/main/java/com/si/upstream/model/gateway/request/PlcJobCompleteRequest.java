package com.si.upstream.model.gateway.request;

import lombok.Data;

/**
 * @author sunxibin
 */
@Data
public class PlcJobCompleteRequest {
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
     * 任务结果：[
     *              0,  //任务完成
     *              1   //任务失败
     *           ]
     */
    private int jobResult;
}
