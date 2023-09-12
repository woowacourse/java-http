package org.apache.coyote.http11.common;

public enum MediaType {

    TEXT_CSS("text/css"),
    TEXT_HTML("text/html"),
    ;

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
