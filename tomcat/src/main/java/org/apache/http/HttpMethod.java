package org.apache.http;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    PATCH,
    ;

    public boolean isSameMethod(HttpMethod httpMethod) {
        return this == httpMethod;
    }
}
