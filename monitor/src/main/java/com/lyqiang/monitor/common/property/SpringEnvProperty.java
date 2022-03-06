package com.lyqiang.monitor.common.property;

import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author lyqiang
 */
@Component
public class SpringEnvProperty {

    private final Environment environment;

    public SpringEnvProperty(Environment environment) {
        this.environment = environment;
    }

    @Nullable
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    @Nullable
    public Boolean getBooleanProperty(String key) {
        return environment.getProperty(key, Boolean.class);
    }

    public Boolean getBooleanProperty(String key, boolean defaultValue) {
        return environment.getProperty(key, Boolean.class, defaultValue);
    }

    @Nullable
    public Integer getIntegerProperty(String key) {
        return environment.getProperty(key, Integer.class);
    }

    public Integer getIntegerProperty(String key, Integer defaultValue) {
        return environment.getProperty(key, Integer.class, defaultValue);
    }

    @Nullable
    public Long getLongProperty(String key) {
        return environment.getProperty(key, Long.class);
    }

    public Long getLongProperty(String key, Long defaultValue) {
        return environment.getProperty(key, Long.class, defaultValue);
    }

}
