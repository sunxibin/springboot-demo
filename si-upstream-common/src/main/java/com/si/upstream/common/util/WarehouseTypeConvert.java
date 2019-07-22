package com.si.upstream.common.util;

import org.springframework.util.NumberUtils;

public class WarehouseTypeConvert {

    public static final Long fromCode(String code) {
        try {
            return NumberUtils.parseNumber(code, Long.class);
        } catch (Exception e) {
            return null;
        }
    }

}
