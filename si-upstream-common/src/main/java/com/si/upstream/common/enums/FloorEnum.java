package com.si.upstream.common.enums;

/**
 * @author sunxibin
 */
public enum FloorEnum {
    /**
     * 二楼
     */
    Floor2("2F"),
    /**
     * 三楼
     */
    Floor3("3F");

    private String floorId;

    FloorEnum(String floorId) {
        this.floorId = floorId;
    }

    public String getFloorId() {
        return this.floorId;
    }

    public static FloorEnum getById(String floorId) {
        for (FloorEnum floor : values()) {
            if (floor.floorId.equalsIgnoreCase(floorId)) {
                return floor;
            }
        }
        return null;
    }
}
