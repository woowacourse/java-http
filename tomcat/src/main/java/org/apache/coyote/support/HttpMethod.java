package org.apache.coyote.support;

import java.util.Arrays;
import org.apache.coyote.exception.HttpMethodNotSupportedException;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod of(final String method) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(method))
                .findFirst()
                .orElseThrow(HttpMethodNotSupportedException::new);
    }

    public boolean isSameMethod(final HttpMethod method) {
        return this == method;
    }
}
