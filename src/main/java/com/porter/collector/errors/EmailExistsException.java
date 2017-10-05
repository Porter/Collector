package com.porter.collector.errors;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException(Exception cause) {
        super(cause);
    }
}
