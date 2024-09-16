package org.apache.coyote.http11;

public enum HttpMessageBodyInfo {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location");

    private final String value;

    HttpMessageBodyInfo(String value) {
        this.value = value;
    }

    public static boolean isContentType(String name) {
        return CONTENT_TYPE.getValue().equals(name);
    }

    public static boolean isContentLength(String name) {
        return CONTENT_LENGTH.getValue().equals(name);
    }

    public String getValue() {
        return value;
    }
}
