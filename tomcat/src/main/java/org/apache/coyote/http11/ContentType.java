package org.apache.coyote.http11;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JAVASCRIPT("text/javascript;charset=utf-8"),
    ICO("image/x-icon")
    ;

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
