package com.si.upstream.common.enums.floor2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Floor2WcsPlcJobMoveType {

    UNKNOWN(Floor2PlcJobTaskStatus.UNKNOWN, Floor2PlcJobTaskStatus.UNKNOWN),

    DIRECT(Floor2PlcJobTaskStatus.EXECUTING, Floor2PlcJobTaskStatus.DONE),

    HEAD(Floor2PlcJobTaskStatus.CACHING, Floor2PlcJobTaskStatus.CACHED),

    TAIL(Floor2PlcJobTaskStatus.EXECUTING, Floor2PlcJobTaskStatus.DONE);

    public final Floor2PlcJobTaskStatus plcTaskStatus;
    public final Floor2PlcJobTaskStatus plcTaskNextStatus;

    private static final Map<String, Floor2WcsPlcJobMoveType> valueMap = new ConcurrentHashMap<>();

    static {
        for (Floor2WcsPlcJobMoveType status : values()) {
            valueMap.put(status.name().toLowerCase(), status);
        }
    }

    Floor2WcsPlcJobMoveType(Floor2PlcJobTaskStatus plcJobStatus, Floor2PlcJobTaskStatus plcTaskNextStatus) {
        this.plcTaskStatus = plcJobStatus;
        this.plcTaskNextStatus = plcTaskNextStatus;
    }

    public static Floor2WcsPlcJobMoveType getByName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        Floor2WcsPlcJobMoveType ret = valueMap.get(name.toLowerCase());
        return ret == null ? UNKNOWN : ret;
    }
}
