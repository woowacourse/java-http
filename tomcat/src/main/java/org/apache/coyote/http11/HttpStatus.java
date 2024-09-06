package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
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
