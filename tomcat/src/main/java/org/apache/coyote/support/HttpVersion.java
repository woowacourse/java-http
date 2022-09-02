package org.apache.coyote.support;

public enum HttpVersion {

    HTTP1("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2.0"),
    HTTP3("HTTP/3.0"),
    ;

    private final String value;

    HttpVersion( String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
