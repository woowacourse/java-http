package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    ;

    private final String httpMethod;

    HttpMethod(final String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public static HttpMethod parseHttpMethodFrom(final String extractedHttpMethod) {
        return Arrays.stream(HttpMethod.values())
                .filter(method -> method.httpMethod.equals(extractedHttpMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Http Method 입니다."));
    }
}
