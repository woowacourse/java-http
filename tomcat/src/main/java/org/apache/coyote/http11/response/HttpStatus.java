package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK("200 OK"),

    FOUND("302 FOUND"),

    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 UNAUTHORIZED"),
    LENGTH_REQUIRED("411 Length Required"),
    ;

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String status() {
        return this.status;
    }
}
