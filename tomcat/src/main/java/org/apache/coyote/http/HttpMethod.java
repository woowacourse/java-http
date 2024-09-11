package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST,
    ;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP Method 입니다. method: %s"
                        .formatted(method)));
    }
}
