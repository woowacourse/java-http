package org.apache.coyote.http11.model;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    ;

    private final String key;

    Header(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
