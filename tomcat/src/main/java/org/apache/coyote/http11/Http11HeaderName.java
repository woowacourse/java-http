package org.apache.coyote.http11;

public enum Http11HeaderName {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    ACCEPT("Accept"),
    ;

    private final String name;

    Http11HeaderName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
