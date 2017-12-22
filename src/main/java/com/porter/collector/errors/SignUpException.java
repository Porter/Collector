package com.porter.collector.errors;

public class SignUpException extends RuntimeException {
    public SignUpException() {
        super();
    }

    public SignUpException(String message) {
        super(message);
    }

    public SignUpException(Exception e) {
        super(e);
    }
}
