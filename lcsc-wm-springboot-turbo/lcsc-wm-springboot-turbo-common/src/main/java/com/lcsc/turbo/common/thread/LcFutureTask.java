package com.lcsc.turbo.common.thread;

import java.util.concurrent.FutureTask;

public class LcFutureTask<T> extends FutureTask<T> {

    public LcFutureTask(TCallable<T> callable) {
        super(callable);
    }

}
