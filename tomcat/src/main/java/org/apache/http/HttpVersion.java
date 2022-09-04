package org.apache.http;

public enum HttpVersion {

    HTTP09("HTTP/0.9"),
    HTTP10("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP20("HTTP/2.0");

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
