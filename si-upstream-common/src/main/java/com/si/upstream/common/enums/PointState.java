package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum PointState {
    /**
     * 空闲（任务完成后标记为起始点位空闲）
     */
    Free(0),
    /**
     * 托盘驶入/驶出中
     */
    Occupying(1),
    /**
     * 已占用（任务完成后标记为目标点位已经占用）
     */
    Occupied(2);


    private int code;

    PointState(Integer code) {
        this.code = code;
    }

    public static PointState getByCode(int code) {
        for (PointState type : values()) {
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
