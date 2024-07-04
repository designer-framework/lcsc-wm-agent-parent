package com.lcsc.turbo.common.thread;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncUtils {

    private static final LcThreadPoolExecutor lcThreadPoolExecutor = new LcThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2
            , 10, TimeUnit.SECONDS
            , new LinkedBlockingDeque<>()
    );

    @SneakyThrows
    public static <T> List<T> doInvokeAll(List<TCallable<T>> tasks) {
        return lcThreadPoolExecutor.doInvokeAll(tasks);
    }

    public static <T> Future<T> submit(TCallable<T> tasks) {
        return lcThreadPoolExecutor.submit(tasks);
    }

    public static <T> Future<T> submit(Callable<T> tasks) {
        return lcThreadPoolExecutor.submit(tasks);
    }

    public static void submit(Runnable task) {
        lcThreadPoolExecutor.submit(task);
    }

    static class LcThreadPoolExecutor extends ThreadPoolExecutor {

        public LcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new CallerRunsPolicy());
        }

        /**
         * 放入任务, 运算并返回所有结果集
         *
         * @param tasks
         * @param <T>
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         */
        public <T> List<T> doInvokeAll(List<TCallable<T>> tasks) throws InterruptedException, ExecutionException {
            //
            List<Future<T>> futures = invokeAll(tasks);

            List<T> tList = new ArrayList<>();
            for (Future<T> future : futures) {
                T t = future.get();
                tList.add(t);
            }

            return tList;
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            if (callable instanceof TCallable) {
                return new LcFutureTask<>((TCallable<T>) callable);
            } else {
                return super.newTaskFor(callable);
            }
        }

        @Override
        public void execute(Runnable command) {
            super.execute(command);
        }

    }


}
