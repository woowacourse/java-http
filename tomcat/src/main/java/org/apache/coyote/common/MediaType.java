package org.apache.coyote.common;

public enum MediaType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css")
    ;

    private final String value;

    MediaType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
