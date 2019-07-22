package com.si.upstream.common.enums.floor2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Floor2PlcJobTaskStatus {

    UNKNOWN,

    WAIT_DISPATCH,

    CACHING,

    CACHED,

    EXECUTING,

    DONE,

    CANCEL;

    private static final Map<String, Floor2PlcJobTaskStatus> valueMap = new ConcurrentHashMap<>();

    static {
        for (Floor2PlcJobTaskStatus status : values()) {
            valueMap.put(status.name().toLowerCase(), status);
        }
    }

    public static Floor2PlcJobTaskStatus getByName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        Floor2PlcJobTaskStatus ret = valueMap.get(name.toLowerCase());
        return ret == null ? UNKNOWN : ret;
    }
}
