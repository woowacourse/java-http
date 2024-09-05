package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    ;

    private final String name;

    private static final Map<String, HttpMethod> SUIT_CASE = Arrays.stream(HttpMethod.values())
            .collect(Collectors.toMap(HttpMethod::getName, Function.identity()));

    HttpMethod(final String name) {
        this.name = name;
    }


    public static HttpMethod fromName(final String target) {
        if (SUIT_CASE.containsKey(target)) {
            return SUIT_CASE.get(target);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP METHOD입니다.");
    }

    public String getName() {
        return name;
    }
}
