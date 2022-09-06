package org.apache.coyote.http11.common;

public enum HeaderKeys {

    HOST("Host"),
    CONNECTION("Connection"),

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    ;

    private final String name;

    HeaderKeys(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
