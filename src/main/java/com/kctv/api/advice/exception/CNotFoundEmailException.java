package com.kctv.api.advice.exception;

public class CNotFoundEmailException extends RuntimeException {
    public CNotFoundEmailException(String msg, Throwable t) {
        super(msg, t);
    }

    public CNotFoundEmailException(String msg) {
        super(msg);
    }

    public CNotFoundEmailException() {
        super();
    }
}
