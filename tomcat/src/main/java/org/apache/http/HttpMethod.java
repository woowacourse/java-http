package org.apache.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE;

    public static HttpMethod fromString(String method) {
        return Arrays.stream(values())
                .filter(httpMethod ->
                        httpMethod.name()
                                .equalsIgnoreCase(method)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP method: " + method)
                );
    }

}
