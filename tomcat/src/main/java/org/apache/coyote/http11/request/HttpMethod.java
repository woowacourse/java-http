package org.apache.coyote.http11.request;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod parse(String method) {
        if (GET.method.equals(method)) {
            return GET;
        }
        return POST;
    }
}
