package org.apache.coyote.http;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(String method) {
        return valueOf(method);
    }

    public boolean isPost() {
        return this == POST;
    }

    public boolean isGet() {
        return this == GET;
    }
}
