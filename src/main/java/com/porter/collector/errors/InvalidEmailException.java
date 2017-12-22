package com.porter.collector.errors;

public class InvalidEmailException extends SignUpException {
    public InvalidEmailException() {

    }

    public InvalidEmailException(Exception e) {
        super(e);
    }
}
