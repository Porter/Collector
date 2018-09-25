package com.porter.collector.exception;

public class CollectionExistsException extends RuntimeException {

    public CollectionExistsException(Exception cause) {
        super(cause);
    }
}
