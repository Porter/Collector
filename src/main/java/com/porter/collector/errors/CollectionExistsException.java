package com.porter.collector.errors;

public class CollectionExistsException extends RuntimeException {

    public CollectionExistsException(Exception cause) {
        super(cause);
    }
}
