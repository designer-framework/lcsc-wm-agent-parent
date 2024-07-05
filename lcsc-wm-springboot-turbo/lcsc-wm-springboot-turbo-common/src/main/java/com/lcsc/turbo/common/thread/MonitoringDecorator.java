package com.lcsc.turbo.common.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
class MonitoringDecorator implements Runnable {

    private final Runnable delegate;

    public MonitoringDecorator(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        delegate.run();
        long endTime = System.currentTimeMillis();

        ThreadPoolExecutor executor = AsyncUtils.EXECUTOR.getThreadPoolExecutor();
        log.error(
                "线程状态-> TaskCount: {}, CompletedTaskCount: {}, ActiveCount: {}, QueueSize: {}, LargestPoolSize: {} 任务执行耗时: {}"
                , executor.getTaskCount(), executor.getCompletedTaskCount(), executor.getActiveCount(), executor.getQueue().size(), executor.getLargestPoolSize()
                , endTime - startTime
        );
    }

}