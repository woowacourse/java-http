package org.apache.coyote.http11;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod of(String method) {
        return valueOf(method);
    }
}
