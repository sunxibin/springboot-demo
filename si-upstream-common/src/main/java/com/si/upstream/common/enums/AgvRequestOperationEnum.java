package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum AgvRequestOperationEnum {
    /**
     * 驶入申请
     */
    entry_application(1),
    /**
     * 驶入完成上报
     */
    entry_completion(2),
    /**
     * 驶出申请
     */
    exit_application(3),
    /**
     * 驶出完成上报
     */
    exit_completion(4);


    private int code;

    AgvRequestOperationEnum(Integer code) {
        this.code = code;
    }

    public static AgvRequestOperationEnum getByCode(int code) {
        for (AgvRequestOperationEnum operation : values()) {
            if (operation.code == code) {
                return operation;
            }
        }
        return null;
    }

    public int getCode() {
        return this.code;
    }
}
