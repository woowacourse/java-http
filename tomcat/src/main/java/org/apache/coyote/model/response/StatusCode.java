package org.apache.coyote.model.response;

public enum StatusCode {

    OK("200"),
    FOUND("302"),
    UNAUTHORIZED("401"),
    ;

    private final String statusCode;

    StatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
