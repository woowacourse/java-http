package org.apache.coyote.http11.common.response;

public enum StatusCode {
    OK("200", "OK"),
    FOUND("302", "Found"),
    NOT_FOUND("404", "Not Found"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed");

    private final String code;
    private final String text;

    StatusCode(String code, String text) {
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
