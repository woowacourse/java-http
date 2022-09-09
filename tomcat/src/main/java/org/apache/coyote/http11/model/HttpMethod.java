package org.apache.coyote.http11.model;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod of(String method) {
        return valueOf(method);
    }
}
