package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(final String input) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(input))
                .findAny()
                .orElseThrow(InvalidHttpMethodException::new);
    }
}
