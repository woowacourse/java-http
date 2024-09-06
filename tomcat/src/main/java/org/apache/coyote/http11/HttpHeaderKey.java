package org.apache.coyote.http11;

public enum HttpHeaderKey {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),

    ;

    private final String name;

    HttpHeaderKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
