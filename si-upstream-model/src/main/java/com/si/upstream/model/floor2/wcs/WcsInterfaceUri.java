package com.si.upstream.model.floor2.wcs;

public enum WcsInterfaceUri {

    ROBOT_MOVE("/api/wcs/standardized/robot/job/submit"),
    CANCEL_JOB("/api/wcs/standardized/robot/job/cancel");

    public final String uri;

    WcsInterfaceUri(String uri) {
        this.uri = uri;
    }
}
