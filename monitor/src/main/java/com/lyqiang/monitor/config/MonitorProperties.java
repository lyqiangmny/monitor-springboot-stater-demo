package com.lyqiang.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lyqiang
 */
@ConfigurationProperties(
        prefix = "lyqiang.monitor"
)
@Component
public class MonitorProperties {

    private String wechatRobotUrl;

    private String wechatTellTo;

    private boolean robotSwitchOpen = true;

    private String appName = "DEFAULT_APP_NAME";

    private Integer recordLogGtMillSecond = 500;

    /**
     * 默认报警规则：接口耗时超过 recordLogGtMillSecond 并且 超过 apiCostAlarmGtMillSecond 则报警
     */
    private Integer apiCostAlarmGtMillSecond = 0;

    /**
     * 是否启用按时间段监测，近 N 时间段内发生 X 次慢请求，则报警，默认不启用
     */
    private boolean apiCostAlarmByPeriod = false;

    /**
     * 报警规则：30秒内超过出现5次慢请求
     */
    private Integer apiCostAlarmCountPeriod = 30 * 1000;

    private Integer apiCostAlarmCountThreshold = 5;

    public String getWechatRobotUrl() {
        return wechatRobotUrl;
    }

    public void setWechatRobotUrl(String wechatRobotUrl) {
        this.wechatRobotUrl = wechatRobotUrl;
    }

    public String getWechatTellTo() {
        return wechatTellTo;
    }

    public void setWechatTellTo(String wechatTellTo) {
        this.wechatTellTo = wechatTellTo;
    }

    public boolean isRobotSwitchOpen() {
        return robotSwitchOpen;
    }

    public void setRobotSwitchOpen(boolean robotSwitchOpen) {
        this.robotSwitchOpen = robotSwitchOpen;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getRecordLogGtMillSecond() {
        return recordLogGtMillSecond;
    }

    public void setRecordLogGtMillSecond(Integer recordLogGtMillSecond) {
        this.recordLogGtMillSecond = recordLogGtMillSecond;
    }

    public Integer getApiCostAlarmGtMillSecond() {
        return apiCostAlarmGtMillSecond;
    }

    public void setApiCostAlarmGtMillSecond(Integer apiCostAlarmGtMillSecond) {
        this.apiCostAlarmGtMillSecond = apiCostAlarmGtMillSecond;
    }

    public Integer getApiCostAlarmCountPeriod() {
        return apiCostAlarmCountPeriod;
    }

    public void setApiCostAlarmCountPeriod(Integer apiCostAlarmCountPeriod) {
        this.apiCostAlarmCountPeriod = apiCostAlarmCountPeriod;
    }

    public Integer getApiCostAlarmCountThreshold() {
        return apiCostAlarmCountThreshold;
    }

    public void setApiCostAlarmCountThreshold(Integer apiCostAlarmCountThreshold) {
        this.apiCostAlarmCountThreshold = apiCostAlarmCountThreshold;
    }

    public boolean isApiCostAlarmByPeriod() {
        return apiCostAlarmByPeriod;
    }

    public void setApiCostAlarmByPeriod(boolean apiCostAlarmByPeriod) {
        this.apiCostAlarmByPeriod = apiCostAlarmByPeriod;
    }
}
