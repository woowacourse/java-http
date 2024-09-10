package org.apache.coyote.http;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String value) {
        return valueOf(value.toUpperCase());
    }

    public String value() {
        return value;
    }
}
