package org.apache.coyote.http11;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(final String value) {
        return HttpMethod.valueOf(value.toUpperCase());
    }
}
