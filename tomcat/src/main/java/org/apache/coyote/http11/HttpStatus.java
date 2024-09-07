package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_EXCEPTION(500),
    ;

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public boolean isClientError() {
        return code % 100 == 4;
    }

    public int getCode() {
        return code;
    }
}
