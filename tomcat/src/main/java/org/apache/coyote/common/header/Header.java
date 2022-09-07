package org.apache.coyote.common.header;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    Header(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
