package com.kctv.api.advice.exception;

public class CPartnerNotFoundException extends RuntimeException {
    public CPartnerNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public CPartnerNotFoundException(String msg) {
        super(msg);
    }

    public CPartnerNotFoundException() {
        super();
    }
}
