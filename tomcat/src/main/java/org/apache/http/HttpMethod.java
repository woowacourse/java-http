package org.apache.http;

import java.util.Arrays;
import java.util.Optional;

public enum HttpMethod {

    GET, POST, PATCH, PUT, DELETE;

    public static Optional<HttpMethod> find(final String value) {
        return Arrays.stream(values())
                .filter(it -> value.startsWith(it.name()))
                .findFirst();
    }
}
