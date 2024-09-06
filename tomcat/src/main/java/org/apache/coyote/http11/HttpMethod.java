package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Optional;

public enum HttpMethod {
    GET,
    POST,
    PATCH,
    DELETE;

    public static Optional<HttpMethod> from(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(name))
                .findAny();
    }
}
