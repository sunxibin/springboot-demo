package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum LetDownFlag {
    /**
     * 不放下货架
     */
    offline,
    /**
     * 放下货架
     */
    online;


    public static LetDownFlag getByName(String name) {
        for (LetDownFlag flag : values()) {
            if (flag.name().equalsIgnoreCase(name)) {
                return flag;
            }
        }
        return null;
    }
}
