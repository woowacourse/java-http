package org.apache.coyote.common;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length")
    ;

    private final String value;

    Header(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
