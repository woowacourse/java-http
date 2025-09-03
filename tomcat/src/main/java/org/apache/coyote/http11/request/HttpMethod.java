package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.http11.exception.UnsupportedHttpMethodException;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    ;

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> Objects.equals(httpMethod.name(), name.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new UnsupportedHttpMethodException(name));
    }
}
