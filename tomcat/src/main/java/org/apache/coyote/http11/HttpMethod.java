package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    HttpMethod() {
    }

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.toString().equals(method))
                .findFirst()
                .orElseThrow();
    }
}
