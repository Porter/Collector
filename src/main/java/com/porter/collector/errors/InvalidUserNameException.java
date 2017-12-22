package com.porter.collector.errors;

public class InvalidUserNameException extends SignUpException {

    public InvalidUserNameException() {

    }

    public InvalidUserNameException(Exception cause) {
        super(cause);
    }
}
