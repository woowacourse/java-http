package org.apache.coyote.http11.common.header;

public enum HeaderName {

    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type");

    private final String propertyName;

    HeaderName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
