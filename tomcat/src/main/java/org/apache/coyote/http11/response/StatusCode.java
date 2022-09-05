package org.apache.coyote.http11.response;

public enum StatusCode {
    OK("200 OK"),
    FOUND("302 FOUND"),
    ;

    private final String message;

    StatusCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
