package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String path;

    private ResponseEntity(HttpStatus httpStatus, String path) {
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public static ResponseEntity of(HttpStatus httpStatus, String path) {
        return new ResponseEntity(httpStatus, path);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }
}
