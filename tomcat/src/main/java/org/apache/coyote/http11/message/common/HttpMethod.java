package org.apache.coyote.http11.message.common;

import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    HEAD,
    CONNECT,
    TRACE;

    public static HttpMethod from(final String method) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(method.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
