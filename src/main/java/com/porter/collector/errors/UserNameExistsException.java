package com.porter.collector.errors;

public class UserNameExistsException extends SignUpException {

    public UserNameExistsException(Exception cause) {
        super(cause);
    }
}
