package org.apache.coyote.http11;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(final String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
