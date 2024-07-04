package com.lcsc.turbo.common.thread;

public class Callback<T> {

    public void onSuccess(T result) {
    }

    public void onException(Exception e) {
        System.out.println("异常" + e);
        System.exit(0);
    }

}
