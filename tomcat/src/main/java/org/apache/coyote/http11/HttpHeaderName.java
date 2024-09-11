package org.apache.coyote.http11;

public enum HttpHeaderName {

    ACCEPT("Accept"),
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ;
    private final String name;

    HttpHeaderName(String name) {
        this.name = name;
    }

    public boolean equalsName(String name) {
        return this.name.equals(name);
    }

    public String getName() {
        return name;
    }
}
