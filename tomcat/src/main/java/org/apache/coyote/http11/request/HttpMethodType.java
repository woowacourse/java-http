package org.apache.coyote.http11.request;

public enum HttpMethodType {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH;

    public static HttpMethodType of(final String method) {
        return HttpMethodType.valueOf(method.toUpperCase());
    }
}
