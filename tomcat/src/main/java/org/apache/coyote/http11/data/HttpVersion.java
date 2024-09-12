package org.apache.coyote.http11.data;

import java.util.Arrays;
import java.util.Optional;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static Optional<HttpVersion> from(String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equals(value))
                .findAny();
    }

    public String getValue() {
        return value;
    }
}
