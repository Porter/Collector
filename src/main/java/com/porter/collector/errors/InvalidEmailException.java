package com.porter.collector.errors;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {

    }

    public InvalidEmailException(Exception e) {
        super(e);
    }
}
