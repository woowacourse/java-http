package org.apache.coyote.http11;

import java.util.Arrays;

public enum ProtocolVersion {
    HTTP1_1("HTTP/1.1"),
    HTTP2("HTTP/2.0");

    private final String value;

    ProtocolVersion(String value) {
        this.value = value;
    }

    public static ProtocolVersion findByVersion(String input) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Protocol version not found - input: " + input));
    }
}
