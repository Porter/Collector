package com.porter.collector.exception;

import java.util.List;

public class MissingHeadersException extends UnableToProcessCsvException {

    private List<String> headers;

    public MissingHeadersException(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getMissingHeaders() {
        return headers;
    }
}
