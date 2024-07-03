package com.lcsc.turbo.common.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-04 02:12
 */
@Getter
public class TurboRunnable implements Runnable {

    private final List<TurboRunnable.TRunnable> callables;

    private final List<Exception> exceptions = new ArrayList<>();

    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final CountDownLatch state;

    public TurboRunnable(List<TurboRunnable.TRunnable> callables) {
        this.callables = callables;
        state = new CountDownLatch(callables.size());
    }

    @Override
    public void run() {
        for (TurboRunnable.TRunnable callable : callables) {
            AsyncUtils.submit(() -> {

                try {
                    callable.run();
                } catch (Exception e) {
                    interrupted.compareAndSet(false, true);
                    exceptions.add(e);
                } finally {
                    state.countDown();
                }

            });
        }
    }

    public void completion() throws InterruptedException {
        state.await();
        if (interrupted.get()) {
            throw new InterruptedException();
        }
    }

    public interface TRunnable {

        public void run() throws Exception;

    }

}
