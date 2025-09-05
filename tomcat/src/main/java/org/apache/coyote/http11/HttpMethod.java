package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH,
    ;

    public static HttpMethod fromHeaderValue(String headerValue) {
        for (HttpMethod value : HttpMethod.values()) {
            if (value.name().equals(headerValue)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 http Method 입니다. :" + headerValue);
    }
}
