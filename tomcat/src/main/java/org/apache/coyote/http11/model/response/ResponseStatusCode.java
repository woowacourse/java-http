package org.apache.coyote.http11.model.response;

public enum ResponseStatusCode {
    OK("200"),
    UNAUTHORIZED("401"),
    FOUND("302"),
    NOT_FOUND("404"),
    ;

    private final String statusCode;

    ResponseStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
