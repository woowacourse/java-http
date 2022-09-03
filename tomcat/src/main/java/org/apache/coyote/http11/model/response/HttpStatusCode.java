package org.apache.coyote.http11.model.response;

public enum HttpStatusCode {
    OK("200"),
    UNAUTHORIZED("401"),
    FOUND("302"),
    ;

    private final String statusCode;

    HttpStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
