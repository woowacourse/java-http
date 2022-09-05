package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200 OK "),
    CREATED("201 CREATED"),
    FOUND("302 FOUND "),
    UNAUTHORIZED("401 UNAUTHORIZED"),
    NOT_FOUND("404 NOT_FOUND");

    public final String code;

    HttpStatus(String code) {
        this.code = code;
    }
}
