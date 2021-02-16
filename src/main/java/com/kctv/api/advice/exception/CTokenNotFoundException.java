package com.kctv.api.advice.exception;

public class CTokenNotFoundException extends RuntimeException {
    public CTokenNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public CTokenNotFoundException(String msg) {
        super(msg);
    }

    public CTokenNotFoundException() {
        super();
    }
}
