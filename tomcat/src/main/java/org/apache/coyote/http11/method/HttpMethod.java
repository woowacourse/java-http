package org.apache.coyote.http11.method;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    UPDATE("UPDATE"),
    PATCH("PATCH"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String method;

    private static final Map<String, HttpMethod> CLASSIFY =
            Arrays.stream(HttpMethod.values())
                    .collect(Collectors.toMap(HttpMethod::name, Function.identity()));

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod from(final String method) {
        final String m = method.toUpperCase();
        return Optional.ofNullable(CLASSIFY.get(m))
                .orElseThrow(() -> new NotFoundHttpMethodException(String.format("%s is Not Support!", m)));
    }
}
