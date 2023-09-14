package org.apache.coyote.common;

public enum HttpHeader {

    LOCATION("Location"),
    COOKIE("Cookies"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_TYPE("Content-Type"),
    CONTEENT_LENGTH("Content-Length");


    private final String name;

    HttpHeader(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
