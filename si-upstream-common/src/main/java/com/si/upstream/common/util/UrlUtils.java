package com.si.upstream.common.util;

import com.si.upstream.common.config.FloorConfiguration;

/**
 * @author sunxibin
 */
public class UrlUtils {
    /**标准搬运的ip*/
    private static String wcs_si = null;
    /**接口平台的ip*/
    private static String interface_platform = null;


    private static synchronized String getWcsSi() {
        if (null == wcs_si) {
            wcs_si = FloorConfiguration.wcs_ip + ":" + FloorConfiguration.wcs_port;
        }
        return wcs_si;
    }

    private static synchronized String getInterfacePlatform() {
        if (null == interface_platform) {
            interface_platform = FloorConfiguration.interface_platform_ip + ":" + FloorConfiguration.interface_platform_port;
        }
        return interface_platform;
    }

    /**
     * AGV请求进站接口
     */
    public static String getRequestEntryUrl() {
        return "http://" + getInterfacePlatform() + "/a51040_accept?type=5";
    }

    /**
     * upstreamJob完成上报接口
     */
    public static String getReportCompletedUrl() {
        return "http://" + getInterfacePlatform() + "/a51040_accept?type=3";
    }

    /**
     * 货架出场接口
     */
    public static String getBucketOutUrl() {
        return "http://" + getWcsSi() + "/api/wcs/standardized/bucket/out";
    }

    /**
     * AGV归巢任务
     */
    public static String getAgvHomeUrl() {
        return "http://" + getWcsSi() + "/api/wcs/standardized/bucket/out";
    }

    /**
     * 货架移位任务下发接口
     * 1、不校验货架的货架移位：包含货架入场
     * 2、货架二段位移任务
     * 3、指定AGV的货架移位任务
     */
    public static String getRobotJobUrl() {
        return "http://" + getWcsSi() + "/api/wcs/standardized/robot/job/submit";
    }

    /**
     * AGV空车移动任务下发接口
     * 1、不指定AGV的空车移动（只有目标点）
     */
    public static String getMoveJobUrl() {
        return "http://" + getWcsSi() + "/api/wcs/standardized/agv/move";
    }

}
