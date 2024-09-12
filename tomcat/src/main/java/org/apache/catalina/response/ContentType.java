package org.apache.catalina.response;

public enum ContentType {

    CSS("text/css"),
    HTML("text/html;charset=utf-8"),
    ;

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType of(String content) {
        if (content.endsWith(".css")) {
            return CSS;
        }
        return HTML;
    }

    @Override
    public String toString() {
        return value;
    }
}
