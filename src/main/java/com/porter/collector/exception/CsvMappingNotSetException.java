package com.porter.collector.exception;

import java.util.List;

public class CsvMappingNotSetException extends UnableToProcessCsvException {

    private final List<String> headers;

    public CsvMappingNotSetException(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getHeaders() {
        return headers;
    }
}
