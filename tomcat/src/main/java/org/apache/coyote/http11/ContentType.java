package org.apache.coyote.http11;

public enum ContentType {
    HTML("text/html;charset=UTF-8"),
    CSS("text/css;charset=UTF-8"),
    JAVASCRIPT("text/javascript;charset=UTF-8"),
    TEXT("text/plain;charset=UTF-8");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType from(String path) {
        if (path.endsWith(".html")) {
            return HTML;
        }
        if (path.endsWith(".css")) {
            return CSS;
        }
        if (path.endsWith(".js")) {
            return JAVASCRIPT;
        }
        return TEXT;
    }
}
