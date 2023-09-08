package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404);

    public final int code;

    HttpStatus(int code) {
        this.code = code;
    }
}
