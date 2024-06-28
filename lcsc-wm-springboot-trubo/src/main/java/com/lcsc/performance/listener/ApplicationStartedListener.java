package com.lcsc.performance.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

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
    public void running(ConfigurableApplicationContext context) {
        log.error("耗时: {}", (System.currentTimeMillis() - startTime) / 1000 + "/s");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
