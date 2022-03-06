package com.lyqiang.monitor.strategy.provider;

import com.lyqiang.monitor.common.alarm.WeChatAlarmUtils;
import com.lyqiang.monitor.config.MonitorProperties;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 接口访问监控
 *
 * @author lyqiang
 */
@Aspect
@Component
public class ProviderMonitor {

    private final MonitorProperties monitorProperties;

    private final SlowApiMonitorRecord slowApiMonitorRecord;

    private static final Logger logger = LoggerFactory.getLogger(ProviderMonitor.class);

    public ProviderMonitor(MonitorProperties monitorProperties, SlowApiMonitorRecord slowApiMonitorRecord) {
        this.monitorProperties = monitorProperties;
        this.slowApiMonitorRecord = slowApiMonitorRecord;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void provider() {
        //
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getProvider() {
        //
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postProvider() {
        //
    }

    @Around("provider()")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint);
    }

    @Around("getProvider()")
    public Object processGet(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint);
    }

    @Around("postProvider()")
    public Object processPost(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint);
    }

    private Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String urlMethod = getUrlMethod(joinPoint);
        String params = getParams(joinPoint);
        long startTime = System.nanoTime();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception ex) {
            // 可以设置一些 bug 异常进行报警
            if (ex instanceof NullPointerException || ex instanceof ClassCastException || ex instanceof NumberFormatException) {
                logger.warn("接口访问异常，URL为：{}，参数为：{}, ex:{}", urlMethod, params, ex);
                WeChatAlarmUtils.sendExceptionAlarm(urlMethod, params, ex);
            }
            throw ex;
        } finally {
            handleFinally(result, startTime, urlMethod, params);
        }
    }

    private void handleFinally(Object result, long startTime, String urlMethod, String params) {
        if (Objects.nonNull(result)) {
            String resultStr = result.toString();
            if (resultStr.length() > 3000) {
                resultStr = resultStr.substring(0, 3000);
            }
            double costTime = (System.nanoTime() - startTime) / 1e6d;
            logAndAlarmIfNecessary(costTime, urlMethod, params, resultStr);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("接口访问结束，URL为：{}，参数为：{}，用时：{}", urlMethod, params, (System.nanoTime() - startTime) / 1e6d);
            }
        }
    }

    private void logAndAlarmIfNecessary(double costTime, String urlMethod, String params, String resultStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("接口访问结束，URL为：{}，参数为：{}，返回值：{}，用时：{}", urlMethod, params, resultStr, costTime);
        }
        Integer recordLogGtMillSecond = monitorProperties.getRecordLogGtMillSecond();
        if (costTime < recordLogGtMillSecond) {
            return;
        }
        logger.info("接口访问结束，URL为：{}，参数为：{}，返回值：{}，用时：{}", urlMethod, params, resultStr, costTime);
        Integer apiCostAlarmGtMillSecond = monitorProperties.getApiCostAlarmGtMillSecond();
        if (apiCostAlarmGtMillSecond > 0 && costTime > apiCostAlarmGtMillSecond) {
            slowApiMonitorRecord.recordSlowAsync(urlMethod, costTime, params);
        }
    }

    private String getUrlMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequestMapping mappingAnnotation = method.getAnnotation(RequestMapping.class);
        GetMapping getMappingAnnotation = method.getAnnotation(GetMapping.class);
        PostMapping postMappingAnnotation = method.getAnnotation(PostMapping.class);

        String[] urlValue = mappingAnnotation != null ? mappingAnnotation.value() :
                getMappingAnnotation != null ? getMappingAnnotation.value() : postMappingAnnotation.value();

        String requestUrl = Arrays.toString(urlValue);
        String requestMethod = mappingAnnotation != null ? Arrays.toString(mappingAnnotation.method()) : StringUtils.EMPTY;
        return requestUrl + " " + requestMethod;
    }

    private String getParams(ProceedingJoinPoint joinPoint) {
        StringBuilder params = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object item = args[i];
            if (item instanceof HttpServletRequest || item instanceof HttpServletResponse) {
                continue;
            }
            if (item == null) {
                params.append("null");
            } else {
                params.append(item);
            }
            if (i < args.length - 1) {
                params.append("| ");
            }
        }
        return params.toString();
    }
}
