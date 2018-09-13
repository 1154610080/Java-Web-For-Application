package com.web.site;

import java.io.UnsupportedEncodingException;

/**
 * 时间工具类
 * 计算时间单位
 *
 * @author Egan
 * @date 2018/9/13 21:41
 **/
public final class TimeUtils {

    public static String intervalToString(long timeInterval) throws UnsupportedEncodingException {
        if(timeInterval < 1_000)
            return new String("小于一秒".getBytes("utf-8"), "utf-8");
        else if(timeInterval < 60_000)
            return (timeInterval) / 1_000 + "秒";
        return new String(("大约 " + (timeInterval / 60_000) + "分钟")
                .getBytes("utf-8"), "utf-8");
    }
}
