package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpVersionType {

    HTTP_0_9("HTTP/0.9"),
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    HTTP_3("HTTP/3");

    private final String value;

    HttpVersionType(final String value) {
        this.value = value;
    }

    public static HttpVersionType getByValue(final String value) {
        return Arrays.stream(HttpVersionType.values())
                .filter(item -> item.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Version 값 입니다. - " + value));
    }

    public String getValue() {
        return value;
    }
}
