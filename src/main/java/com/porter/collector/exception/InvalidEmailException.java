package com.porter.collector.exception;

public class InvalidEmailException extends SignUpException {
    public InvalidEmailException() {

    }

    public InvalidEmailException(Exception e) {
        super(e);
    }
}
