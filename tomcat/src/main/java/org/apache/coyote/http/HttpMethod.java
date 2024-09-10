package org.apache.coyote.http;

public enum HttpMethod {
    GET,
    POST,
    ;

    public static HttpMethod from(final String value) {
        return valueOf(value.toUpperCase());
    }
}
