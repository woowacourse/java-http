package org.apache.coyote.http11;

public enum HttpVersion {

    HTTP11("HTTP/1.1");

    private String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
