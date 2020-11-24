package com.kctv.api.advice.exception;

public class CNotVerifyEmailException extends RuntimeException {
    public CNotVerifyEmailException(String msg, Throwable t) {
        super(msg, t);
    }

    public CNotVerifyEmailException(String msg) {
        super(msg);
    }

    public CNotVerifyEmailException() {
        super();
    }
}
