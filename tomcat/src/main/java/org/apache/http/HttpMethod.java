package org.apache.http;

import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    ;

    public static HttpMethod from(final String httpMethod) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(value.name(), httpMethod.toUpperCase()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
