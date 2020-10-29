package com.kctv.api.advice.exception;

public class CIncorrectPasswordException extends RuntimeException {
    public CIncorrectPasswordException(String msg, Throwable t) {
        super(msg, t);
    }

    public CIncorrectPasswordException(String msg) {
        super(msg);
    }

    public CIncorrectPasswordException() {
        super();
    }
}
