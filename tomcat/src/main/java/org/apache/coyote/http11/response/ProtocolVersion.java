package org.apache.coyote.http11.response;

public enum ProtocolVersion {
    HTTP1("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2.0"),
    ;

    private final String value;

    ProtocolVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
