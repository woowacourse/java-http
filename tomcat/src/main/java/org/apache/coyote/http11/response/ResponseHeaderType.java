package org.apache.coyote.http11.response;

public enum ResponseHeaderType {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length")
    ;

    private final String type;

    ResponseHeaderType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
