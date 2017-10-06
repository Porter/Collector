package com.porter.collector.errors;

public class InvalidUserNameException extends RuntimeException {

    public InvalidUserNameException() {

    }

    public InvalidUserNameException(Exception cause) {
        super(cause);
    }
}
