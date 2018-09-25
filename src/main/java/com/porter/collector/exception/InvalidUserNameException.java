package com.porter.collector.exception;

public class InvalidUserNameException extends SignUpException {

    public InvalidUserNameException() {

    }

    public InvalidUserNameException(Exception cause) {
        super(cause);
    }
}
