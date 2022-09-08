package org.apache.coyote.http11.support;

public enum HttpMime {

    DEFAULT("*/*"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript");

    private final String value;

    HttpMime(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
