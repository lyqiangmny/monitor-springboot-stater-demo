package com.lyqiang.monitor.common.utils;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 注意：只能在容器初始化完成后使用
 *
 * @author lyqiang
 */
public class SpringContextUtil implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static ConfigurableApplicationContext context;

    private SpringContextUtil() {
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        context = configurableApplicationContext;
    }
}
