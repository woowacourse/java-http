package org.apache.coyote.request;

import java.util.Arrays;
import org.apache.coyote.exception.NoSuchHttpMethodException;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod of(final String httpMethod) {
        return Arrays.stream(values())
                .filter(value -> value.isSame(httpMethod))
                .findFirst()
                .orElseThrow(() -> new NoSuchHttpMethodException());
    }

    private boolean isSame(final String httpMethod) {
        return value.equals(httpMethod);
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
