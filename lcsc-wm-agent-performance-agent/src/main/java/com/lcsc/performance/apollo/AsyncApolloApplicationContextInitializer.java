package com.lcsc.performance.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer;
import com.lcsc.performance.utils.AsyncUtils;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.util.concurrent.Future;

public class AsyncApolloApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        try {

            for (Future<Config> mConfigFuture : AsyncLoadConfigManager.m_configFutures) {
                mConfigFuture.get();
            }

        } finally {
            AsyncUtils.shutdown();
        }
    }

    @Override
    public int getOrder() {
        return ApolloApplicationContextInitializer.DEFAULT_ORDER + 1;
    }

}
