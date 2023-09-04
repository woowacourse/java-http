package org.apache.coyote.http11.response;

public enum StatusCode {

    OK("200", "OK"),
    FOUND("302", "FOUND"),
    BAD_REQUEST("400", "BAD_REQUEST"),
    UNAUTHORIZED("401", "UNAUTHORIZED"),
    NOT_FOUND("404", "NOT_FOUND"),
    INTERNAL_SERVER_ERROR("500", "INTERNAL_SERVER_ERROR");

    private final String code;
    private final String text;

    StatusCode(final String code, final String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
