package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum PlcJobStatus {
    /**
     * 初始化
     */
    INIT,
    /**
     * 等待内侧点位任务完成
     */
    WAIT_PRE_TASK_COMPLETION,
    /**
     * 等待调度
     */
    WAIT_DISPATCH,
    /**
     * 执行中
     */
    EXECUTING,
    /**
     * 任务完成
     */
    DONE,
    /**
     * 任务取消
     */
    CANCEL;


    public static PlcJobStatus getByName(String name) {
        for (PlcJobStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
