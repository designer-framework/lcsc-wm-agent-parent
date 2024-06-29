package com.lcsc.turbo.apollo;

import com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer;
import lombok.SneakyThrows;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link ApolloApplicationContextInitializer}
 */
public class AsyncApolloApplicationContextInitializer extends ApolloApplicationContextInitializer implements EnvironmentPostProcessor, ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    @SneakyThrows
    protected void initialize(ConfigurableEnvironment environment) {
        super.initialize(environment);
        if (initialized.compareAndSet(false, true)) {
            for (Future<?> mConfigFuture : ConfigManagerInjectorCustomizer.m_configFutures) {
                mConfigFuture.get();
            }
        }
    }

    @Override
    public int getOrder() {
        return ApolloApplicationContextInitializer.DEFAULT_ORDER - 1;
    }

}
