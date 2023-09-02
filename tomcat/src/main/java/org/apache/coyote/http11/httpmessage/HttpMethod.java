package org.apache.coyote.http11.httpmessage;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String target) {
        return Arrays.stream(values())
                .filter(each -> each.value.equals(target))
                .findFirst()
                .orElseThrow();
    }

    public boolean isEqualTo(HttpMethod other) {
        return this == other;
    }
}
