package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod of(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.getValue().equals(method))
                .findFirst()
                .orElseThrow(MethodNotAllowedException::new);
    }

    public String getValue() {
        return value;
    }
}
