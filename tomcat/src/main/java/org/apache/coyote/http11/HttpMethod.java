package org.apache.coyote.http11;

import org.apache.coyote.http11.parser.HttpFormatException;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod convertFrom(String httpMethod) {
        if (!httpMethod.toUpperCase().equals(httpMethod)) {
            throw new HttpFormatException();
        }
        for (HttpMethod method : values()) {
            if (method.method.equals(httpMethod)) {
                return method;
            }
        }
        throw new HttpFormatException();
    }
}
