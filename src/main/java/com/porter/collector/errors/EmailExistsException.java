package com.porter.collector.errors;

public class EmailExistsException extends SignUpException {

    public EmailExistsException(Exception cause) {
        super(cause);
    }
}
