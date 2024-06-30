package com.lcsc.turbo.swagger.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;

/**
 * @description: 如果可以, 更好的做法是直接排除这个配置类: Swagger2DocumentationConfiguration。 (目前没办法通过Spring生命周期强行排除)
 * @author: Designer
 * @date : 2024-06-29 16:32
 * @see springfox.documentation.swagger2.annotations.EnableSwagger2
 */
public class SwaggerTurboApplicationRunListener implements SpringApplicationRunListener {

    private static final String SWAGGER_TURBO_MAP_PROPERTY_SOURCE = "SwaggerTurboMapPropertySource";

    private static final MapPropertySource disableSwaggerPropertySource = new MapPropertySource(
            SWAGGER_TURBO_MAP_PROPERTY_SOURCE
            , Collections.singletonMap("springfox.documentation.auto-startup", "false") //
    );

    public SwaggerTurboApplicationRunListener(SpringApplication application, String[] args) {
    }

    /**
     * @param context the application context
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#SPRINGFOX_DOCUMENTATION_AUTO_STARTUP
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#isAutoStartup()
     */
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        addOrSkipSwaggerTurboPropertySources(context.getEnvironment().getPropertySources());
    }

    private void addOrSkipSwaggerTurboPropertySources(MutablePropertySources propertySources) {
        if (!propertySources.contains(SWAGGER_TURBO_MAP_PROPERTY_SOURCE)) {
            propertySources.addLast(disableSwaggerPropertySource);
        }
    }

}
