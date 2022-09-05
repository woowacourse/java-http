package org.apache.coyote.http11.support;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import java.util.Arrays;

public enum HttpVersion {

    HTTP09("HTTP/0.9"),
    HTTP10("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP20("HTTP/2.0");

    private final String name;

    HttpVersion(final String name) {
        this.name = name;
    }

    public static HttpVersion from(final String name) {
        return Arrays.stream(values())
                .filter(it -> it.name.equals(name))
                .findAny()
                .orElseThrow(InvalidHttpRequestException::new);
    }

    public String getName() {
        return name;
    }
}
