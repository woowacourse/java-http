package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String path;

    public ResponseEntity(HttpStatus httpStatus, String path) {
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }

}
