package com.lcsc.turbo.common.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AsyncUtils {

    private static final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

    static {
        threadPoolTaskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        threadPoolTaskExecutor.setQueueCapacity(0);
        threadPoolTaskExecutor.initialize();
    }

    public static void shutdown() {
        threadPoolTaskExecutor.shutdown();
    }

    public static <T> Future<T> submit(Callable<T> tCallable) {
        return threadPoolTaskExecutor.submit(tCallable);
    }

}
