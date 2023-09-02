package org.apache.coyote.http11.common;

public enum ContentType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
