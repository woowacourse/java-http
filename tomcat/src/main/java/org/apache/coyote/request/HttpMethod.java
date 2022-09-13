package org.apache.coyote.request;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.equals(HttpMethod.valueOf(method)))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
