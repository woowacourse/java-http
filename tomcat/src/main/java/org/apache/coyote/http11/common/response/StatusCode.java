package org.apache.coyote.http11.common.response;

public enum StatusCode {
    OK("200", "OK"),
    FOUND("302", "Found");

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
