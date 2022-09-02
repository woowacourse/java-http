package org.apache.coyote.http11;

import java.util.Arrays;

public enum RequestMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static boolean isIn(final String line) {
        return Arrays.stream(values())
                .anyMatch(method -> line.contains(method.name()));
    }
}
