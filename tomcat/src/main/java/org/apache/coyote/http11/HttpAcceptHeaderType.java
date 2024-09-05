package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpAcceptHeaderType {
    PLAIN("text/plain"),
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript"),
    SVG("image/svg+xml"),
    ALL_ACCEPT("*/*");

    private final String value;

    HttpAcceptHeaderType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HttpAcceptHeaderType fromValue(final String input) {
        return Arrays.stream(HttpAcceptHeaderType.values())
                .filter(value -> value.getValue().equals(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Accept Header 타입입니다."));
    }
}
