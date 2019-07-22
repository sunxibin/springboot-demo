package com.si.upstream.common.enums.floor2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Floor2WcsMoveJobStatus {

    UNKNOWN,

    WAIT_PUSH,

    PUSHED,

    DONE,

    CANCEL;

    private static final Map<String, Floor2WcsMoveJobStatus> valueMap = new ConcurrentHashMap<>();

    static {
        for (Floor2WcsMoveJobStatus status : values()) {
            valueMap.put(status.name().toLowerCase(), status);
        }
    }

    public static Floor2WcsMoveJobStatus getByName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        Floor2WcsMoveJobStatus ret = valueMap.get(name.toLowerCase());
        return ret == null ? UNKNOWN : ret;
    }
}
