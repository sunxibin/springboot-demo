package com.si.upstream.model.wcs.result;

import lombok.Data;

/**
 * @author sunxibin
 */
@Data
public class SimpleResult {
    /**
     * 成功失败标识
     */
    private boolean success;
    /**
     * 错误码
     */
    private String code;
    /**
     * 错误信息
     */
    private String message;
}
