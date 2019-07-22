package com.si.upstream.model.floor2.wcs;

import lombok.Data;

@Data
public class Floor2WcsCancelJob {

    private String executeMode = "";

    private Integer jobType;

    private String reason;

    private String robotJobId;

    private Integer warehouseId;

    public static Floor2WcsCancelJob jobCreate(String robotJobId) {
        Floor2WcsCancelJob item = new Floor2WcsCancelJob();
        item.setRobotJobId(robotJobId);
        return item;
    }


//    {
//        "executeMode": "",
//            "jobType": 0,
//            "reason": "",
//            "robotJobId": "",
//            "warehouseId": 0
//    }

}
