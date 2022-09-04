package org.apache.http;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST, PATCH, PUT, DELETE;

    public static boolean isStartWith(final String value) {
        return Arrays.stream(values())
                .anyMatch(it -> value.startsWith(it.name()));
    }
}
