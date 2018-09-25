package com.porter.collector.exception;

public class EmailExistsException extends SignUpException {

    public EmailExistsException(Exception cause) {
        super(cause);
    }
}
