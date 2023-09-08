package org.apache.coyote.http11.response;

public enum ContentType {
    CSS("text/css"),
    HTML("text/html"),
    HTML_UTF8("text/html;charset=utf-8"),
    TEXT_PLAIN("text/plain"),
    JAVASCRIPT("text/javascript");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getType() {
        return value;
    }
}
