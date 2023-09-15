package org.apache.coyote.common;

import java.util.Arrays;
import org.apache.coyote.exception.InvalidHttpMethodException;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(final String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findAny()
                .orElseThrow(InvalidHttpMethodException::new);
    }
}
