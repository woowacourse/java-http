package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200 OK"),

    FOUND("302 FOUND"),

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
