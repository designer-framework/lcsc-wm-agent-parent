package com.lcsc.turbo.swagger.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-29 13:39
 */
@Configuration
public class DisableEndpointEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String DISABLE_ENDPOINT = "DisableEndpointTurboPropertySource";

    private static final MapPropertySource DISABLE_ENDPOINT_TURBO_PROPERTY_SOURCE;

    static {
        Map<String, Object> turboPropertyMap = new HashMap<>();
        turboPropertyMap.put("management.endpoints.enabledByDefault", "false");
        turboPropertyMap.put("management.health.defaults.enabled", "false");
        DISABLE_ENDPOINT_TURBO_PROPERTY_SOURCE = new MapPropertySource(DISABLE_ENDPOINT, turboPropertyMap);
    }


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(DISABLE_ENDPOINT_TURBO_PROPERTY_SOURCE);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
