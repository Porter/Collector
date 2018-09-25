package com.porter.collector.exception;

public class UserNameExistsException extends SignUpException {

    public UserNameExistsException(Exception cause) {
        super(cause);
    }
}
