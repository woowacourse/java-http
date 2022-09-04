package org.apache.coyote.http11.domain;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static boolean isIn(final String line) {
        return Arrays.stream(values())
                .anyMatch(method -> line.contains(method.name()));
    }
}
