package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    NONE;

    public static HttpMethod findByName(final String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findAny()
                .orElse(NONE);
    }
}
