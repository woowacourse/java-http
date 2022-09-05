package org.apache.coyote.http11.response.spec;

public enum HttpStatus {
    OK(200),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
