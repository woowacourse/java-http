package org.apache.coyote.http11.common;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401)
    ;

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
}
