package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized");

    private final String code;
    private final String message;

    HttpStatus(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
