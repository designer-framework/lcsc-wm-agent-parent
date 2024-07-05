package com.lcsc.turbo.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Future;

/**
 * {@link ApolloApplicationContextInitializer}
 */
@Slf4j
public class AsyncApolloApplicationContextInitializer extends ApolloApplicationContextInitializer implements EnvironmentPostProcessor, ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final String ApolloTurboPropertySource = "ApolloTurboPropertySource";

    private static final MapPropertySource enabledApolloBootstrapPropertySource = new MapPropertySource(
            ApolloTurboPropertySource
            , Collections.singletonMap(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, "true") //
    );

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        addApolloTurboPropertySources(environment.getPropertySources());
        super.initialize(context);
    }

    private void addApolloTurboPropertySources(MutablePropertySources propertySources) {
        if (!propertySources.contains(ApolloTurboPropertySource)) {
            propertySources.addLast(enabledApolloBootstrapPropertySource);
        }
    }

    @Override
    protected void initialize(ConfigurableEnvironment environment) {
        super.initialize(environment);
        concurrentInitializeConfig();
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
