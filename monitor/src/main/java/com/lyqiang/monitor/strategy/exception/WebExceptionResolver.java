package com.lyqiang.monitor.strategy.exception;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lyqiang
 * 处理未捕获的异常
 */
@Component
@Order(Integer.MIN_VALUE)
public class WebExceptionResolver implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(WebExceptionResolver.class);

    private static final String UNKNOWN_STR = "unKnown";

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String ip = getIpAddress(request);
        String uri = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();

        Map<String, Object> attribute = new HashMap<>(4);
        String code = "50000";
        attribute.put("code", code);
        attribute.put("message", "exception: " + e.getMessage());

        logger.warn("本地接口异常  -- [{}] -- [{}] 访问 [{}] 出现异常, 入参:{}, 异常信息如下: {}",
                code, ip, uri, JSON.toJSONString(parameterMap), e);

        attribute.put("success", false);
        attribute.put("data", null);

        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setAttributesMap(attribute);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        return modelAndView;
    }

    private String getIpAddress(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xFor = request.getHeader("X-Forwarded-For");

        if (StringUtils.isNotEmpty(xFor) && !UNKNOWN_STR.equalsIgnoreCase(xFor)) {
            int index = xFor.indexOf(",");
            return index != -1 ? xFor.substring(0, index) : xFor;
        }

        xFor = xip;
        if (StringUtils.isNotEmpty(xip) && !UNKNOWN_STR.equalsIgnoreCase(xip)) {
            return xip;
        }

        if (isBlankOrUnknown(xip)) {
            xFor = request.getHeader("Proxy-Client-IP");
        }

        if (isBlankOrUnknown(xFor)) {
            xFor = request.getHeader("WL-Proxy-Client-IP");
        }

        if (isBlankOrUnknown(xFor)) {
            xFor = request.getHeader("HTTP_CLIENT_IP");
        }

        if (isBlankOrUnknown(xFor)) {
            xFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (isBlankOrUnknown(xFor)) {
            xFor = request.getRemoteAddr();
        }
        return xFor;
    }

    private boolean isBlankOrUnknown(String str) {
        return StringUtils.isBlank(str) || UNKNOWN_STR.equalsIgnoreCase(str);
    }
}
