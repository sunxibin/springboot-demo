package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum BucketMoveType {
    /**
     * 货架入场不校验货架的货架移位任务 ： startPoint, endPoint, online或者offline     //不传(bucketCode, agvEndPoint, agvCode)
     */
    without_bucket(1),
    /**
     * 货架二段移位任务 ： bucketCode, endPoint, agvEndPoint, offline                 //不传(startPoint, agvCode)
     */
    secondary_move(2),
    /**
     * 指定AGV的货架移位 ： agvCode, startPoint, endPoint, offline                    //不传(bucketCode，agvEndPoint)
     */
    with_agv(3),
    /**
     * 普通货架移位 ：bucketCode, endPoint                                            //不传(agvCode, agvEndPoint, startPoint)
     */
    normal(4);

    private int code;

    BucketMoveType(Integer code) {
        this.code = code;
    }

    public static BucketMoveType getByCode(int code) {
        for (BucketMoveType type : values()) {
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
