package org.apache.coyote.common;

import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.exception.http.HttpMethodNotMatchException;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    PATCH,
    CONNECT,
    TRACE;

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> Objects.equals(httpMethod.name(), method))
                .findFirst()
                .orElseThrow(HttpMethodNotMatchException::new);
    }
}
