package org.apache.coyote.http11;

public enum HeaderElement {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    HeaderElement(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
