package org.apache.coyote;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
