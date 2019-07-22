package com.si.upstream.application.vo;

import lombok.Data;

/**
 * 用料区到补料区
 *
 * @author sunxibin
 */
@Data
public class ConsumerToProducerVO {
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 库区编码
     */
    private String zoneCode;
    /**
     * 用料区作业台编码
     */
    private String sourceStationCode;
    /**
     * 用料区点位编码
     */
    private String sourcePointCode;
    /**
     * 补料区作业台编码
     * 二楼：必填
     * 三楼：不填        //三楼其实为出入场点
     * 小龙坎：必填
     */
    private String targetStationCode;
    /**
     * 叫料的次序 : 第一次叫料（0），第二次叫料（1）
     * 第二次叫料的优先级最高
     */
    private Integer order;
}
