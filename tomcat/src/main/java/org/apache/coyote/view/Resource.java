package org.apache.coyote.view;

import org.apache.coyote.response.HttpStatus;

public class Resource {

    private final String value;
    private final HttpStatus httpStatus;

    public Resource(String value, HttpStatus httpStatus) {
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
