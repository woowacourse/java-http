package org.apache.coyote.http11;

public enum HttpRequestMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    ;

    public static HttpRequestMethod from(final String value) {
        return HttpRequestMethod.valueOf(value.toUpperCase());
    }
}
