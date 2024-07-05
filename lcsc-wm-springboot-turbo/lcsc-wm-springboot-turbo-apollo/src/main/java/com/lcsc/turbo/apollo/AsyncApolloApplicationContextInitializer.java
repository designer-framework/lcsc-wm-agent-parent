package com.lcsc.turbo.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * {@link ApolloApplicationContextInitializer}
 */
@Slf4j
public class AsyncApolloApplicationContextInitializer extends ApolloApplicationContextInitializer implements EnvironmentPostProcessor, ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final String ApolloTurboPropertySource = "ApolloTurboPropertySource";

    private static final MapPropertySource enabledApolloBootstrapPropertySource;

    static {
        Map<String, Object> turboPropertyMap = new HashMap<>();
        turboPropertyMap.put(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, "true");
        turboPropertyMap.put(PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, "true");
        enabledApolloBootstrapPropertySource = new MapPropertySource(ApolloTurboPropertySource, turboPropertyMap);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment configurableEnvironment, SpringApplication springApplication) {
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        if (!propertySources.contains(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
            addApolloTurboPropertySources(propertySources);
            super.postProcessEnvironment(configurableEnvironment, springApplication);
        }
    }

    @Override
    protected void initialize(ConfigurableEnvironment environment) {
        super.initialize(environment);
        concurrentInitializeConfig();
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
    }

    private void addApolloTurboPropertySources(MutablePropertySources propertySources) {
        if (!propertySources.contains(ApolloTurboPropertySource)) {
            propertySources.addLast(enabledApolloBootstrapPropertySource);
        }
    }

    @Override
    public int getOrder() {
        return ApolloApplicationContextInitializer.DEFAULT_ORDER - 1;
    }

    @SneakyThrows
    protected void concurrentInitializeConfig() {
        //
        Collection<Future<Config>> values = AsyncConfigManagerInjectorCustomizer.m_configFutureMap.values();

        for (Future<Config> future : values) {

            if (!future.isDone()) {
                Config config = future.get();
            }

        }

    }

}
