package org.apache.coyote.http11.response;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JAVASCRIPT("text/javascript;charset=utf-8");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
