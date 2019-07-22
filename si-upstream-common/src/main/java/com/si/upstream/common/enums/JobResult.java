package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum JobResult {
    /**
     * 接受成功
     */
    success(1),
    /**
     * 接受失败
     */
    failure(9),
    /**
     * 可以驶入/驶出
     */
    allow_entry(3),
    /**
     * 不可以驶入/驶出
     */
    no_entry(2);


    private int code;

    JobResult(Integer code) {
        this.code = code;
    }

    public static JobResult getByCode(int code) {
        for (JobResult result : values()) {
            if (result.code == code) {
                return result;
            }
        }
        return null;
    }

    public int getCode() {
        return this.code;
    }
}
