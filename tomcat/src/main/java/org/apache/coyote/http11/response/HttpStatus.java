package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200),
    NOT_FOUND(404);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }
}
