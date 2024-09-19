package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200 Ok"),
    FOUND("302 Found"),
    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 Unauthorized"),
    NOT_FOUND("404 Not Found"),
    METHOD_NOT_SUPPORTED("405 Method Not Supported"),
    ;

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
