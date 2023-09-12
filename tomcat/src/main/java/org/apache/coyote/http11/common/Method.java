package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Optional;

public enum Method {

    GET,
    PUT,
    POST,
    DELETE,
    HEAD,
    TRACE,
    OPTIONS,
    PATCH;

    public static Optional<Method> find(final String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
