package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum ProtocolVersion {
    HTTP1("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2.0"),
    ;

    private final String value;

    ProtocolVersion(String value) {
        this.value = value;
    }

    public static ProtocolVersion from(String value) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(value))
                .findAny()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }
}
