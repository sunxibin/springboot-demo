package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum InnerJobType {
    /**
     * 空车移位
     */
    MOVE,
    /**
     * 货架移位
     */
    BUCKET_MOVE;

    public static InnerJobType getByName(String name) {
        for (InnerJobType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
