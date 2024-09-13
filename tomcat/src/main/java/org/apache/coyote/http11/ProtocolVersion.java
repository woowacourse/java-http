package org.apache.coyote.http11;

public enum ProtocolVersion {
    HTTP1_1("HTTP/1.1"),
    HTTP2("HTTP/2.0");

    private final String value;

    ProtocolVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
