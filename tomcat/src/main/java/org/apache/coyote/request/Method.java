package org.apache.coyote.request;

import java.util.Arrays;
import org.apache.coyote.exception.MethodNotAllowedException;

public enum Method {
    GET("GET"),
    POST("POST");

    private final String name;

    Method(final String name) {
        this.name = name;
    }

    public static Method getMethod(final String input) {
        return Arrays.stream(Method.values())
                .filter(method -> method.name.equals(input))
                .findFirst()
                .orElseThrow(() -> new MethodNotAllowedException(input));
    }
}
