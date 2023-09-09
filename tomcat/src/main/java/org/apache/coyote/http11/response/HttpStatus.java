package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK("200"),
    FOUND("302"),
    BAD_REQUEST("400"),
    UNAUTHORIZED("401"),
    NOT_FOUND("404"),
    ;

    private final String code;

    HttpStatus(String code) {
        this.code = code;
    }

    public String getCodeWithMessage() {
        return this.code + " " + this.name();
    }
}
