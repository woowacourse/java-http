package org.apache.coyote.http11;

public enum StatusCode {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    BAD_REQUEST(400)
    ;

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
