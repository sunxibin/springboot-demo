package com.si.upstream.common.enums.floor2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Floor2PlcJobTaskType {

    UNKNOWN,

    PRODUCE,

    CONSUME;

    private static final Map<String, Floor2PlcJobTaskType> valueMap = new ConcurrentHashMap<>();

    static {
        for (Floor2PlcJobTaskType status : values()) {
            valueMap.put(status.name().toLowerCase(), status);
        }
    }

    public static Floor2PlcJobTaskType getByName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        Floor2PlcJobTaskType ret = valueMap.get(name.toLowerCase());
        return ret == null ? UNKNOWN : ret;
    }
}
