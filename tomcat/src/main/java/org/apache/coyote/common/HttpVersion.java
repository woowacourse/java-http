package org.apache.coyote.common;

public enum HttpVersion {

    HTTP11("HTTP/1.1")
    ;

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
