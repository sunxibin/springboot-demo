package com.si.upstream.application.vo;

import lombok.Data;

/**
 * 补料区到用料区
 *
 * @author sunxibin
 */
@Data
public class ProducerToConsumerVO {
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 库区编码
     */
    private String zoneCode;
    /**
     * 补料区作业台编码
     */
    private String sourceStationCode;
    /**
     * 补料区点位编码 （三楼为货架出入场点）
     */
    private String sourcePointCode;
    /**
     * 用料区作业台编码
     */
    private String targetStationCode;
    /**
     * 叫料的次序 : 补料区到用料区逻辑应该不需要
     */
    private Integer order;
}
