package org.apache.coyote.http11;

public enum Header {

    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    ACCEPT("Accept"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    Cache_Control("Cache-Control"),
    ;

    private final String name;

    Header(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
