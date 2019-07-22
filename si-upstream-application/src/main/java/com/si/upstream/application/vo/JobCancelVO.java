package com.si.upstream.application.vo;

import lombok.Data;

/**
 * 三楼任务都用 sourcePointCode, 起始点相同的任务都要取消
 *
 * @author sunxibin
 */
@Data
public class JobCancelVO {
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 库区编码
     */
    private String zoneCode;
    /**
     * 发起方作业台编码
     */
    private String sourceStationCode;
    /**
     * 发起方点位编码
     */
    private String sourcePointCode;
}
