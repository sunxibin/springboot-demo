package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum StationType {
    /**
     * 出入场点
     */
    InOutArea(0),
    /**
     * 人工炒料区
     */
    ManualOperationArea(1),
    /**
     * 机械手炒料区
     */
    MechanicalOperationArea(2),
    /**
     * 补料区
     */
    BULIAODIAN(3),
    /**
     * 缓存区
     */
    CacheArea(4);


    private int code;

    StationType(Integer code) {
        this.code = code;
    }

    public static StationType getByCode(int code) {
        for (StationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    public int getCode() {
        return this.code;
    }
}
