package com.lcsc.turbo.swagger.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import java.util.Collections;
import java.util.Map;

/**
 * @description: 更好的做法是直接排除这个配置类, Swagger2DocumentationConfiguration
 * @author: Designer
 * @date : 2024-06-29 16:32
 * @see springfox.documentation.swagger2.annotations.EnableSwagger2
 */
public class SwaggerTurboApplicationRunListener implements SpringApplicationRunListener {

    private static final Map<String, Object> DISABLE_SWAGGER = Collections.singletonMap("springfox.documentation.auto-startup", "false");

    private static final String SWAGGER_TURBO_MAP_PROPERTY_SOURCE = "SwaggerTurboMapPropertySource";

    public SwaggerTurboApplicationRunListener(SpringApplication application, String[] args) {
    }

    /**
     * @param context the application context
     * @see DocumentationPluginsBootstrapper#SPRINGFOX_DOCUMENTATION_AUTO_STARTUP
     */
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        context.getEnvironment().getPropertySources().addLast(new MapPropertySource(SWAGGER_TURBO_MAP_PROPERTY_SOURCE, DISABLE_SWAGGER));
    }

}
