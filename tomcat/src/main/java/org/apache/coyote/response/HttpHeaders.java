package org.apache.coyote.response;

public enum HttpHeaders {

    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
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
