package org.apache.coyote.model.response;

public enum StatusCode {

    OK("200"),
    FOUND("302"),
    UNAUTHORIZED("401"),
    NOT_FOUND("404"),
    METHOD_NOT_ALLOWED("405"),
    INTERNAL_SERVER_ERROR("500"),
    ;

    private final String statusCode;

    StatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
