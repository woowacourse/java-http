package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT;

    public static HttpMethod of(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
