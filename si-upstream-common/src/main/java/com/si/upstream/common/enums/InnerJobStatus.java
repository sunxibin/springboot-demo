package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum InnerJobStatus {
    /**
     * 初始化
     */
    INIT,
    /**
     * 已经下发wcs
     */
    SEND,
    /**
     * wcs上报完成
     */
    DONE,
    /**
     * 任务失败
     */
    FAILURE;

    public static InnerJobStatus getByName(String name) {
        for (InnerJobStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
