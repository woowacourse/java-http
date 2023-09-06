package org.apache.coyote.http;

import java.util.Arrays;

public enum Protocol {

    HTTP_1_1("HTTP/1.1");

    private final String name;

    Protocol(final String name) {
        this.name = name;
    }

    public static Protocol from(final String target) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.name.equalsIgnoreCase(target))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 프로토콜 입니다."));
    }
}
