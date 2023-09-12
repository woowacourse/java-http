package org.apache.response;

public enum HttpStatus {

    OK("200"),
    FOUND("302"),
    BAD_REQUEST("400"),
    UNAUTHORIZED("401"),
    NOT_FOUND("404"),
    METHOD_NOT_ALLOWED("405");

    private final String code;

    HttpStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
