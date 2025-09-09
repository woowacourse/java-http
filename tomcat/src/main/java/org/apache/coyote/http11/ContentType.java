package org.apache.coyote.http11;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    TEXT("text/plain;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JAVASCRIPT("application/javascript"),
    JSON("application/json");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
