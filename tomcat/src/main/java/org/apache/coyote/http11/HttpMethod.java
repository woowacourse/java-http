package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.HttpSpecException.HttpMethodException;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PATCH("PATCH"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(HttpMethodException::new);
    }
}
