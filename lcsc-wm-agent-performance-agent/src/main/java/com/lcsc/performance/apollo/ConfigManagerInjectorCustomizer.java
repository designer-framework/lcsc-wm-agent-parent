package com.lcsc.performance.apollo;

import com.ctrip.framework.apollo.internals.ConfigManager;
import com.ctrip.framework.apollo.spi.ApolloInjectorCustomizer;

public class ConfigManagerInjectorCustomizer implements ApolloInjectorCustomizer {

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return ConfigManager.class.equals(clazz) ? (T) new AsyncLoadConfigManager() : null;
    }

    @Override
    public <T> T getInstance(Class<T> clazz, String name) {
        return null;
    }

}
