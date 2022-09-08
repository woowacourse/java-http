package org.apache.coyote.http11.request;

import java.util.stream.Stream;
import org.apache.coyote.http11.exception.HttpVersionNotSupportException;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(final String value) {
        return Stream.of(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new HttpVersionNotSupportException(value));
    }

    public String getValue() {
        return value;
    }
}
