package com.si.upstream.core.floor2;

import lombok.Data;

@Data
public class WcsStandardResponse {

    private String robotJobId;

    private Boolean success;

    private String errorCode;

    private String code;

    public boolean ok() {
        return success == Boolean.TRUE;
    }

    public static WcsStandardResponse emptyResult() {
        WcsStandardResponse response = new WcsStandardResponse();
        response.success = false;
        response.errorCode = "ERR_RESPONSE_EMPTY";
        return response;
    }

}
