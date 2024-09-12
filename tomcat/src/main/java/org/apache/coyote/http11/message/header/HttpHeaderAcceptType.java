package org.apache.coyote.http11.message.header;

import java.util.Arrays;

public enum HttpHeaderAcceptType {
    PLAIN("text/plain"),
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript"),
    SVG("image/svg+xml"),
    ICO("image/x-icon"),
    ALL_ACCEPT("*/*");

    private final String value;

    HttpHeaderAcceptType(final String value) {
        this.value = value;
    }

    public static HttpHeaderAcceptType getByValue(final String value) {
        return Arrays.stream(HttpHeaderAcceptType.values())
                .filter(item -> item.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Header Accept 값 입니다. - " + value));
    }

    public String getValue() {
        return value;
    }
}
