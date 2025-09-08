package org.apache.coyote.http;

public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static HttpMethod from(final String name) {
        return valueOf(name.toUpperCase());
    }
}
