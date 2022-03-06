package com.lyqiang.monitor.common.alarm;

import com.alibaba.fastjson.JSON;
import com.lyqiang.monitor.common.utils.SpringContextUtil;
import org.apache.commons.lang3.ClassUtils;

/**
 * @author lyqiang
 */
public class WeChatAlarmUtils {

    private static final WeChatRobotAlarmSender WE_CHAT_ROBOT_ALARM_SENDER
            = SpringContextUtil.getBean("weChatRobotAlarmSender", WeChatRobotAlarmSender.class);

    private WeChatAlarmUtils() {
    }

    /**
     * 通用报警
     */
    public static void sendGeneralAlarm(String description, String detailMessage) {
        WE_CHAT_ROBOT_ALARM_SENDER.sendMarkDownMessage(description, detailMessage);
    }

    /**
     * 异常
     */
    public static void sendExceptionAlarm(String url, Object param, Exception ex) {
        WE_CHAT_ROBOT_ALARM_SENDER.sendMarkDownMessage("接口发生 " + ClassUtils.getSimpleName(ex) + " 异常, " + url,
                "请求参数：" + JSON.toJSONString(param));
    }

    /**
     * api 耗时报警
     */
    public static void sendApiCostAlarm(String url, double costTime, Object param) {
        WE_CHAT_ROBOT_ALARM_SENDER.sendMarkDownMessage("接口耗时超过阈值, cost: " + costTime + " ms, url: " + url,
                "请求参数：" + JSON.toJSONString(param));
    }

    /**
     * api 耗时报警
     */
    public static void sendApiCostAlarm(String url, Integer period, Integer times, double costTime, Object param) {
        WE_CHAT_ROBOT_ALARM_SENDER.sendMarkDownMessage("接口耗时超过阈值, 近 " + (period / 1000) + " 秒内达到 " + times + " 次," +
                        " 本次请求 cost: " + costTime + " ms, url: " + url,
                "请求参数: " + JSON.toJSONString(param));
    }

}
