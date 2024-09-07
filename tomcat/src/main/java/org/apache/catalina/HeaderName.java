package org.apache.catalina;

public enum HeaderName {
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),

    ;

    private final String value;

    HeaderName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
