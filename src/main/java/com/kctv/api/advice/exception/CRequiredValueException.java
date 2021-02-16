package com.kctv.api.advice.exception;

public class CRequiredValueException extends RuntimeException {
    public CRequiredValueException(String msg, Throwable t) {
        super(msg, t);
    }

    public CRequiredValueException(String msg) {
        super(msg);
    }

    public CRequiredValueException() {
        super();
    }
}
