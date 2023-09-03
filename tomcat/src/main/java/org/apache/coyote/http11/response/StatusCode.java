package org.apache.coyote.http11.response;

public enum StatusCode {

    OK("200", "OK");

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
