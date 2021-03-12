package com.kctv.api.advice.exception;

public class CNotFoundCodeException extends RuntimeException {
    public CNotFoundCodeException(String msg, Throwable t) {
        super(msg, t);
    }

    public CNotFoundCodeException(String msg) {
        super(msg);
    }

    public CNotFoundCodeException() {
        super();
    }
}
