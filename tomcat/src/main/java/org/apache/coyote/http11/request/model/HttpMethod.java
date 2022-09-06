package org.apache.coyote.http11.request.model;

import java.util.Arrays;
import org.apache.coyote.exception.MethodNotAllowedException;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod of(final String value) {
        return Arrays.stream(HttpMethod.values())
                .filter(it -> it.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MethodNotAllowedException(value));
    }

    public boolean isGet() {
        return this == GET;
    }
}
