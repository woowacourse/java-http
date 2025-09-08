package org.apache.coyote.http11.domain;

public enum HttpProtocol {
    HTTP1_1("HTTP/1.1"),
    ;
    private final String value;

    HttpProtocol(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
