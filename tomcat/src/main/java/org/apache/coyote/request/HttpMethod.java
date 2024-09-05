package org.apache.coyote.request;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST,
    ;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid HTTP method: " + method));
    }
}
