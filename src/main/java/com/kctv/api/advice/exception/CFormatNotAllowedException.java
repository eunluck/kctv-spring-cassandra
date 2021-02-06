package com.kctv.api.advice.exception;

public class CFormatNotAllowedException extends RuntimeException {
    public CFormatNotAllowedException(String msg, Throwable t) {
        super(msg, t);
    }

    public CFormatNotAllowedException(String msg) {
        super(msg);
    }

    public CFormatNotAllowedException() {
        super();
    }
}
