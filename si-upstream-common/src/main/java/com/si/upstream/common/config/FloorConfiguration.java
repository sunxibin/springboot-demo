package com.si.upstream.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sunxibin
 */
@Component
public class FloorConfiguration {
    /**
     * wcs的ip和端口
     */
    public static String wcs_ip;
    public static String wcs_port;
    /**
     * 接口平台的ip和端口
     */
    public static String interface_platform_ip;
    public static String interface_platform_port;
    /**
     * 楼层和zoneCode的映射关系
     */
    public static String floor2ZoneCode;
    public static String floor3ZoneCode;

    @Value("${si-upstream.zone-code.floor3:}")
    public void setFloor3ZoneCode(String zoneCode) {
        floor3ZoneCode = zoneCode;
    }

    @Value("${si-upstream.zone-code.floor2:}")
    public void setFloor2ZoneCode(String zoneCode) {
        floor2ZoneCode = zoneCode;
    }

    @Value("${wcs.si.ip}")
    public void setWcsIp(String wcsIp) {
        wcs_ip = wcsIp;
    }

    @Value("${wcs.si.port}")
    public void setWcsPort(String wcsPort) {
        wcs_port = wcsPort;
    }

    @Value("${interface.platform.ip}")
    public void setInterfacePlatformIp(String interfacePlatformIp) {
        interface_platform_ip = interfacePlatformIp;
    }

    @Value("${interface.platform.port}")
    public void setInterfacePlatformPort(String interfacePlatformPort) {
        interface_platform_port = interfacePlatformPort;
    }
}
