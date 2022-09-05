package org.apache.coyote.http11.response.header;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    ;

    private final String name;

    Header(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
