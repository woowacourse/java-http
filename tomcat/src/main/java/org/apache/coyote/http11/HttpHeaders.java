package org.apache.coyote.http11;

public enum HttpHeaders {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location")
    ;

    private final String name;

    HttpHeaders(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
