package org.apache.coyote.http11.request;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT;

    public static HttpMethod fromName(String name) {
        try {
            return HttpMethod.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid HTTP Method: " + name + e.getMessage());
        }
    }
}
