package org.apache.coyote;

import java.util.Arrays;

public enum Protocol {
    HTTP11("HTTP/1.1");

    private final String value;

    Protocol(final String value) {
        this.value = value;
    }

    public static Protocol getProtocol(final String input) {
        return Arrays.stream(Protocol.values())
                .filter(protocol -> protocol.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Protocol"));
    }

    public String getValue() {
        return value;
    }
}
