package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Optional;

public enum Protocol {

    HTTP11("HTTP/1.1");

    private final String value;

    Protocol(final String value) {
        this.value = value;
    }

    public static Optional<Protocol> find(final String value) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.getValue().equals(value))
                .findFirst();
    }

    public String getValue() {
        return value;
    }
}
