package com.lcsc.turbo.common.utils;

import java.util.concurrent.*;

public class AsyncUtils {

    private static final ExecutorService THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2
            , Runtime.getRuntime().availableProcessors() * 2
            , 10, TimeUnit.SECONDS
            , new LinkedBlockingDeque<>()
            , new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void shutdown() {
        THREAD_POOL_EXECUTOR.shutdown();
    }

    public static <T> Future<T> submit(Callable<T> tCallable) {
        return THREAD_POOL_EXECUTOR.submit(tCallable);
    }

    public static void submit(Runnable tCallable) {
        THREAD_POOL_EXECUTOR.submit(tCallable);
    }

}
