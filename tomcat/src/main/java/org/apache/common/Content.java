package org.apache.common;

public class Content {

    private final String value;
    private final HttpStatus httpStatus;

    public Content(String value, HttpStatus httpStatus) {
        this.value = value;
        this.httpStatus = httpStatus;
    }

    public String getValue() {
        return value;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
