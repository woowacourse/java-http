package org.apache.coyote.response;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public String getHttpStatus() {
        return code + " " + name();
    }
}
