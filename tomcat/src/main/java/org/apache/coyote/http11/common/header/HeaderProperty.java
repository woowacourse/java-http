package org.apache.coyote.http11.common.header;

public enum HeaderProperty {

    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type");

    private final String propertyName;

    HeaderProperty(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
