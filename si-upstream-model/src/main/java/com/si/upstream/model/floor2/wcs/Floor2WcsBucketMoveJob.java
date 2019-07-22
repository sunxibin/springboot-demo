package com.si.upstream.model.floor2.wcs;

import lombok.Data;

@Data
public class Floor2WcsBucketMoveJob {

    private String bucketCode;

    private Integer checkCode = 0;

    private String endPoint;

    private String letDownFlag = "offline";

    private String robotJobId = "";

    private String transportEntityType = "BUCKET";

    private Long warehouseId;

    private Integer workFace = 2;

    private String zoneCode;

    private String source = "2F";

    public static Floor2WcsBucketMoveJob jobCreate(String bucketCode, String endPoint, String robotJobId, Long warehouseId, String zoneCode) {
        Floor2WcsBucketMoveJob item = new Floor2WcsBucketMoveJob();
        item.setBucketCode(bucketCode);
        item.setEndPoint(endPoint);
        item.setRobotJobId(robotJobId);
        item.setZoneCode(zoneCode);
        item.setWarehouseId(warehouseId);
        return item;
    }
}
