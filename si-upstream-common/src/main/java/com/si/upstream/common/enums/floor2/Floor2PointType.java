package com.si.upstream.common.enums.floor2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Floor2PointType {

    UNKNOWN,

    GENERAL,

    STATION,

    CACHE;

    private static final Map<String, Floor2PointType> valueMap = new ConcurrentHashMap<>();

    static {
        for (Floor2PointType type : values()) {
            valueMap.put(type.name().toLowerCase(), type);
        }
    }

    public static Floor2PointType getByName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        Floor2PointType ret = valueMap.get(name.toLowerCase());
        return ret == null ? UNKNOWN : ret;
    }
}
