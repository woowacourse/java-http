package org.apache.coyote;

import java.util.Arrays;

public enum ProtocolVersion {

    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2");

    private String value;

    ProtocolVersion(String value) {
        this.value = value;
    }

    public static ProtocolVersion from(String raw) {
        return Arrays.stream(ProtocolVersion.values())
                .filter(protocolVersion -> protocolVersion.value.equals(raw))
                .findFirst()
                .orElseThrow();
    }

    public String value() {
        return value;
    }
}
