package org.apache.coyote.http11;

public enum HttpStatus {
    NOT_FOUND(404),
    FORBIDDEN(403),
    OK(200),
    ;

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
