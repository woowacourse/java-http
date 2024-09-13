package org.apache.coyote.http11.header;

public enum HeaderContent {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location")
    ;

    private final String message;

    HeaderContent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
