package com.lcsc.turbo.common.thread;

import org.springframework.util.concurrent.ListenableFutureCallback;

public class Callback<T> implements ListenableFutureCallback<T> {
    private String id;

    public Callback(String id) {
        this.id = id;
    }

    @Override
    public void onSuccess(T result) {
    }

    @Override
    public void onFailure(Throwable e) {
        System.out.println("异常" + e);
        System.exit(0);
    }

}
