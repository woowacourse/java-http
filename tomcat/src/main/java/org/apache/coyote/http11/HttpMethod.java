package org.apache.coyote.http11;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS;

    public static HttpMethod from(String value) {
        return HttpMethod.valueOf(value.toUpperCase());
    }
}
