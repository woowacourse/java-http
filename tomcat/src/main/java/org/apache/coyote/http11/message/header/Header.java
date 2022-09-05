package org.apache.coyote.http11.message.header;

public enum Header {

    Accept("Accept"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String name;

    Header(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
