package org.apache.coyote.common;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location");

    private final String value;

    Header(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
