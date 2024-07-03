package com.lcsc.turbo.common.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-29 02:46
 */
@Slf4j
public class ApplicationStartedListener implements SpringApplicationRunListener, Ordered {

    private final long startTime;

    public ApplicationStartedListener(SpringApplication application, String[] args) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void starting() {
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        log.error("耗时: {}", (System.currentTimeMillis() - startTime) / 1000 + "/s");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
