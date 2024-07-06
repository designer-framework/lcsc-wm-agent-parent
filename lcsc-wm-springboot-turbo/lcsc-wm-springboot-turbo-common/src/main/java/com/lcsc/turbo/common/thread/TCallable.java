package com.lcsc.turbo.common.thread;

import lombok.Getter;

import java.util.concurrent.Callable;

@Getter
public abstract class TCallable<T> implements Callable<T> {

    private final Callback<T> callback;

    public TCallable(Callback<T> callback) {
        this.callback = callback;
    }

    public String getId() {
        return callback.getId();
    }

    @Override
    public final T call() {
        try {
            T t = doCall();
            callback.onSuccess(t);
            return t;
        } catch (Exception e) {
            callback.onFailure(e);
            return null;
        }
    }

    public abstract T doCall() throws Exception;

}
