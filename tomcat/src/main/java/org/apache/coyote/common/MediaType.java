package org.apache.coyote.common;

public enum MediaType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css");

    private final String source;

    MediaType(final String source) {
        this.source = source;
    }

    public String source() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }
}
