package com.si.upstream.model.gateway.response;

import lombok.Data;

/**
 * @author sunxibin
 */
@Data
public class Response {
    /**
     * 仓库编号
      */
    private String warehouseCode;
    /**
     * 库区编号
     */
    private String zoneCode;
    /**
     * 调用结果：[
     *              0,  //接收成功
     *              9   //接收失败
     *           ]
     */
    private Integer result;

    public static Builder newBuilder() {
        return new Builder();
    }

    private static class Builder {
        private String warehouseCode;
        private String zoneCode;
        private Integer result;

        public Builder() { }

        public void warehouseCode(String warehouseCode) {
            this.warehouseCode = warehouseCode;
        }

        public void zoneCode(String zoneCode) {
            this.zoneCode = zoneCode;
        }

        public void result(Integer result) {
            this.result = result;
        }

        public Response build() {
            Response response = new Response();
            response.warehouseCode = this.warehouseCode;
            response.zoneCode = this.zoneCode;
            response.result = this.result;
            return response;
        }
    }

}
