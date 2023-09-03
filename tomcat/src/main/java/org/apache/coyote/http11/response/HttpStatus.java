package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200),
    FOUND(302);

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
