package com.kctv.api.advice.exception;

public class COverlapSnsKey extends RuntimeException {
    public COverlapSnsKey(String msg, Throwable t) {
        super(msg, t);
    }

    public COverlapSnsKey(String msg) {
        super(msg);
    }

    public COverlapSnsKey() {
        super();
    }
}
