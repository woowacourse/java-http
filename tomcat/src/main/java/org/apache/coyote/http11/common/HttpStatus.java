package org.apache.coyote.http11.common;

public enum HttpStatus {
    OK("200"),
    FOUND("302"),
    UNAUTHORIZED("401"),
    NOT_FOUND("404"),
    CONFLICT("409");

    private final String code;

    HttpStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
