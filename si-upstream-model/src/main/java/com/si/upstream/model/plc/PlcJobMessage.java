package com.si.upstream.model.plc;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlcJobMessage {

    private Integer type;

    private String warehouseCode;

    private String zoneCode;

    private String sourceStationCode;

    private String sourcePointCode;

    private String targetStationCode;

    private String targetPointCode;

    private Integer result;

    private Integer jobResult;

    private String pointCode;

    private String stationCode;

    private Integer operation;

    private Integer order;

    private String _inner_debug_error_desc;

    public static PlcJobMessage result(PlcFrameData<PlcJobMessage> frame, boolean success) {
        return result(frame == null ? null : frame.getBody(), success);
    }

    public static PlcJobMessage result(PlcJobMessage message, boolean success) {
        message = message == null ? new PlcJobMessage() : message;
        return result(message.getWarehouseCode(), message.getZoneCode(), success);
    }

    public static PlcJobMessage result(String warehouseCode, String zoneCode, boolean success) {
        return new PlcJobMessage().setWarehouseCode(warehouseCode).setZoneCode(zoneCode).setResult(success ? 1 : 9);
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public PlcJobMessage setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
        return this;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public PlcJobMessage setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public PlcJobMessage setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getSourceStationCode() {
        return sourceStationCode;
    }

    public PlcJobMessage setSourceStationCode(String sourceStationCode) {
        this.sourceStationCode = sourceStationCode;
        return this;
    }

    public String getSourcePointCode() {
        return sourcePointCode;
    }

    public PlcJobMessage setSourcePointCode(String sourcePointCode) {
        this.sourcePointCode = sourcePointCode;
        return this;
    }

    public String getTargetStationCode() {
        return targetStationCode;
    }

    public PlcJobMessage setTargetStationCode(String targetStationCode) {
        this.targetStationCode = targetStationCode;
        return this;
    }

    public String getTargetPointCode() {
        return targetPointCode;
    }

    public void setTargetPointCode(String targetPointCode) {
        this.targetPointCode = targetPointCode;
    }

    public Integer getResult() {
        return result;
    }

    public PlcJobMessage setResult(Integer result) {
        this.result = result;
        return this;
    }

    public Integer getJobResult() {
        return jobResult;
    }

    public PlcJobMessage setJobResult(Integer jobResult) {
        this.jobResult = jobResult;
        return this;
    }

    public String getPointCode() {
        return pointCode;
    }

    public PlcJobMessage setPointCode(String pointCode) {
        this.pointCode = pointCode;
        return this;
    }

    public String getStationCode() {
        return stationCode;
    }

    public PlcJobMessage setStationCode(String stationCode) {
        this.stationCode = stationCode;
        return this;
    }

    public Integer getOperation() {
        return operation;
    }

    public PlcJobMessage setOperation(Integer operation) {
        this.operation = operation;
        return this;
    }

    public Integer getOrder() {
        return order;
    }

    public PlcJobMessage setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public String get_inner_debug_error_desc() {
        return _inner_debug_error_desc;
    }

    public PlcJobMessage set_inner_debug_error_desc(String _inner_debug_error_desc) {
        this._inner_debug_error_desc = _inner_debug_error_desc;
        return this;
    }

}
