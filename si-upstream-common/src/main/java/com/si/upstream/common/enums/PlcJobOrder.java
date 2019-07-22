package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum PlcJobOrder {
    /**
     * 第一次叫料
     */
    first(1),
    /**
     * 第二次叫料
     */
    second(2);


    private int code;

    PlcJobOrder(Integer code) {
        this.code = code;
    }

    public static PlcJobOrder getByCode(int code) {
        for (PlcJobOrder order : values()) {
            if (order.code == code) {
                return order;
            }
        }
        return null;
    }

    public int getCode() {
        return this.code;
    }
}
