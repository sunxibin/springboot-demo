package com.si.upstream.common.enums;

/**
 * 定义任务类型的目的是在任务入库的时候就完成任务优先级的划分(适用于三楼):
 * second_time_to_mechanical > second_time_away_mechanical > first_time_to_consumer > first_time_away_consumer
 *
 * @author sunxibin
 */
public enum PlcJobType {
    /**
     * 补料 (用料区到补料区)
     */
    ConsumerToProducer(1),
    /**
     * 用料 (补料区到用料区)
     */
    ProducerToConsumer(2),

    /**
     * 二次补货去机械手区域的任务
     */
    second_time_to_mechanical(3),
    /**
     * 二次补货离开机械手区域的任务
     */
    second_time_away_mechanical(4),
    /**
     * 首次补货去机械手+人工区域的任务
     */
    first_time_to_consumer(5),
    /**
     * 首次补货离开机械手+人工区域的任务
     */
    first_time_away_consumer(6);


    private int code;

    PlcJobType(Integer code) {
        this.code = code;
    }

    public static PlcJobType getByCode(int code) {
        for (PlcJobType type : values()) {
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
