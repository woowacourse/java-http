package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK("200 OK"),

    FOUND("302 FOUND"),

    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 UnAuthorized"),
    NOT_FOUND("404 Not Found"),
    LENGTH_REQUIRED("411 Length Required"),

    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    ;

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String status() {
        return this.status;
    }
}
