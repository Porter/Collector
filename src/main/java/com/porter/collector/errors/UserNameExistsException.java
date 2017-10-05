package com.porter.collector.errors;

public class UserNameExistsException extends RuntimeException {

    public UserNameExistsException(Exception cause) {
        super(cause);
    }
}
