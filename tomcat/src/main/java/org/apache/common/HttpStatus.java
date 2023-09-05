package org.apache.common;

public enum HttpStatus {

    OK("200"),
    FOUND("302"),
    UNAUTHORIZED("401"),
    METHOD_NOT_ALLOWED("405");

    private final String code;

    HttpStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
