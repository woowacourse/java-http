package org.apache.coyote.http11.message.header;

import java.util.Arrays;

public enum HttpHeaderConnectionType {
    KEEP_ALIVE("keep-alive"),
    CLOSE("close"),
    UPGRADE("Upgrade"),
    TE("TE");

    private final String value;

    HttpHeaderConnectionType(final String value) {
        this.value = value;
    }

    public static HttpHeaderConnectionType getByValue(final String value) {
        return Arrays.stream(HttpHeaderConnectionType.values())
                .filter(item -> item.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Header Connection 값 입니다. - " + value));
    }

    public String getValue() {
        return value;
    }
}
