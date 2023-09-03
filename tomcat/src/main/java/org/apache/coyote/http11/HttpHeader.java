package org.apache.coyote.http11;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
