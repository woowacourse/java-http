package org.apache.coyote.http11;

public enum HttpHeaders {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    ACCEPT("Accept"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String name;

    HttpHeaders(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
