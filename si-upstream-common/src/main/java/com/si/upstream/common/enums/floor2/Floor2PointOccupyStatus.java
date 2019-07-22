package com.si.upstream.common.enums.floor2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Floor2PointOccupyStatus {

    UNKNOWN,

    USABLE,

    OCCUPIED;

    private static final Map<String, Floor2PointOccupyStatus> valueMap = new ConcurrentHashMap<>();

    static {
        for (Floor2PointOccupyStatus status : values()) {
            valueMap.put(status.name().toLowerCase(), status);
        }
    }

    public static Floor2PointOccupyStatus getByName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        Floor2PointOccupyStatus ret = valueMap.get(name.toLowerCase());
        return ret == null ? UNKNOWN : ret;
    }
}
