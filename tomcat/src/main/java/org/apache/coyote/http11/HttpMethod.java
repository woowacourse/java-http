package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(hm -> hm.method.equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 HttpMethod 입니다." + method));
    }

    public boolean isPost() {
        return this.equals(POST);
    }

    public boolean isGet() {
        return this.equals(GET);
    }
}
