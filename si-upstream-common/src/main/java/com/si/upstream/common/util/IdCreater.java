package com.si.upstream.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * robotJobId生成工具
 * @author sunxibin
 */
public class IdCreater {

    public static final String prefix = "inner-";
    public static SimpleDateFormat baseDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//    public static AtomicLong counter = new AtomicLong(0);


    /**
     * 获取一个任务ID
     * @return
     */
    public static String getInnerJobId() {
        String sDate = null;
        synchronized (baseDateFormat) {
            sDate = baseDateFormat.format(new Date());
        }
        return prefix + sDate;
    }
}
