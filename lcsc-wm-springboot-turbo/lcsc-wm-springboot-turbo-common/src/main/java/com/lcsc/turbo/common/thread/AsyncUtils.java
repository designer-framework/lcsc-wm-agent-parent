package com.lcsc.turbo.common.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class AsyncUtils {

    static final ThreadPoolTaskExecutor EXECUTOR;

    static {
        ThreadPoolTaskExecutor lcThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        lcThreadPoolTaskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 4);
        lcThreadPoolTaskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 4);
        lcThreadPoolTaskExecutor.setKeepAliveSeconds(10);
        lcThreadPoolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        lcThreadPoolTaskExecutor.setTaskDecorator(MonitoringDecorator::new);
        lcThreadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        lcThreadPoolTaskExecutor.initialize();
        EXECUTOR = lcThreadPoolTaskExecutor;

        Runtime.getRuntime().addShutdownHook(new Thread(EXECUTOR::shutdown));
    }

    public static <T> void doInvokeAll(List<TCallable<T>> tasks) {
        doInvokeAll(tasks, null);
    }

    public static <T> void doInvokeAll(List<TCallable<T>> callables, Callback<T> callback) {
        //
        if (callback == null) {
            for (TCallable<T> callable : callables) {
                Future<T> submit = EXECUTOR.submit(callable);
            }
        } else {
            for (TCallable<T> callable : callables) {
                ListenableFuture<T> tListenableFuture = EXECUTOR.submitListenable(callable);
                tListenableFuture.addCallback(new ListenableFutureCallback<T>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        callback.onException(ex);
                    }

                    @Override
                    public void onSuccess(T result) {
                        callback.onSuccess(result);
                    }
                });
            }
        }
    }

    public static <T> Future<T> submit(TCallable<T> tasks) {
        return EXECUTOR.submit(tasks);
    }

    public static <T> Future<T> submit(Callable<T> tasks) {
        return EXECUTOR.submit(tasks);
    }

    public static void submit(Runnable task) {
        EXECUTOR.submit(task);
    }

}
