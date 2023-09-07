package org.apache.coyote.protocol;

import java.util.Arrays;

public enum Protocol {
    HTTP11("HTTP/1.1"),
    ;

    final String value;

    Protocol(final String value) {
        this.value = value;
    }

    public static Protocol from(final String value) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new RuntimeException("해당 프로토콜은 지원하지 않습니다."));
    }

    public String getValue() {
        return value;
    }
}
