package com.lyqiang.monitor.common.alarm;

import com.alibaba.fastjson.JSONObject;
import com.lyqiang.monitor.common.property.SpringEnvProperty;
import com.lyqiang.monitor.common.utils.DateUtils;
import com.lyqiang.monitor.common.utils.IpAddressUtils;
import com.lyqiang.monitor.config.MonitorProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author lyqiang
 */
@Component
public class WeChatRobotAlarmSender {

    private final RestTemplate restTemplate;

    private final MonitorProperties monitorProperties;

    private final SpringEnvProperty springEnvProperty;

    public WeChatRobotAlarmSender(@Qualifier("weChatRestTemplate") RestTemplate restTemplate,
                                  MonitorProperties monitorProperties, SpringEnvProperty springEnvProperty) {
        this.restTemplate = restTemplate;
        this.monitorProperties = monitorProperties;
        this.springEnvProperty = springEnvProperty;
    }

    public void sendMarkDownMessage(String description, String errorMsg) {

        //配置文件或配置中心中可以控制报警的开关
        boolean robotSwitchOpen = springEnvProperty.getBooleanProperty("lyqiang.monitor.robot-switch-open", true);
        if (!robotSwitchOpen) {
            return;
        }

        if (StringUtils.isBlank(monitorProperties.getWechatRobotUrl())) {
            return;
        }

        StringBuilder content = new StringBuilder("应用<font color=\"warning\">")
                .append(monitorProperties.getAppName())
                .append("</font>告警，请关注。\n")
                .append(">描述: <font color=\"comment\">")
                .append(description)
                .append("</font>\n")
                .append(">异常信息: <font color=\"comment\">")
                .append(errorMsg)
                .append("</font>\n")
                .append(">IP: <font color=\"comment\">")
                .append(IpAddressUtils.getIpAddress())
                .append("</font>\n")
                .append(">时间: <font color=\"comment\">")
                .append(DateUtils.now())
                .append("</font> \n");

        if (StringUtils.isNotBlank(monitorProperties.getWechatTellTo())) {
            content.append(monitorProperties.getWechatTellTo());
        }

        JSONObject markdown = new JSONObject();
        markdown.put("content", content);
        JSONObject requestJson = new JSONObject();
        requestJson.put("msgtype", "markdown");
        requestJson.put("markdown", markdown);

        HttpEntity<JSONObject> httpRequest = new HttpEntity<>(requestJson);
        restTemplate.postForEntity(monitorProperties.getWechatRobotUrl(), httpRequest, String.class);
    }

}
