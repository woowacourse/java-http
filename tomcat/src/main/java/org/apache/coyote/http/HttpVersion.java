package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Objects;

public enum HttpVersion implements HttpComponent {

    HTTP11("HTTP/1.1"),
    ;

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String value) {
        return Arrays.stream(values())
                .filter(httpVersion -> Objects.equals(httpVersion.value, value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String asString() {
        return value;
    }
}
