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

    public static HttpMethod getHttpMethod(String requestMethod) {
        for (HttpMethod value : HttpMethod.values()) {
            if (value.name().equals(requestMethod)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }
}
