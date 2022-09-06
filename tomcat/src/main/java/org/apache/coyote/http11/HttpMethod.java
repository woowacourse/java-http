package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod findMethod(final String httpMethod) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(httpMethod))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
