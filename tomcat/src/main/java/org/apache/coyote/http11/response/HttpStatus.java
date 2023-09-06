package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK("200"),
    CREATED("201"),
    FOUND("302"),
    UNAUTHORIZED("401"),
    ;

    private final String statusCode;

    HttpStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
