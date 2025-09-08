package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS;

    public static HttpMethod fromString(final String method) {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method cannot be null");
        }
        
        try {
            return valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
}
