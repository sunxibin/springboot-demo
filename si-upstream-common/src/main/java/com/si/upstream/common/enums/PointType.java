package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum PointType {
    /**
     * 内侧点位
     */
    InsidePoint(0),
    /**
     * 外侧点位
     */
    OutsidePoint(1),
    /**
     * 工作站前一个点位
     */
    PreviousPoint(2),
    /**
     * 其他点位
     */
    OtherPoint(3);


    private int code;

    PointType(Integer code) {
        this.code = code;
    }

    public static PointType getByCode(int code) {
        for (PointType type : values()) {
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
