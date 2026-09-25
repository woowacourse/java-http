package org.apache.coyote.http11.request;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, CONNECT;

    public static HttpMethod from(final String name) {
        return HttpMethod.valueOf(name.toUpperCase());
    }
}
