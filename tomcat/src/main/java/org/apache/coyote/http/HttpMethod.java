package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod from(final String requestValue) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(requestValue))
                .findFirst()
                .orElseThrow();
    }

    public boolean isGet() {
        return value.equals(GET.value);
    }

    public boolean isPost() {
        return value.equals(POST.value);
    }
}
