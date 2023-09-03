package org.apache.coyote.http11.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RequestMethod {

    GET("GET");

    private final String method;

    RequestMethod(String method) {
        this.method = method;
    }

    private static final Map<String, RequestMethod> methods =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(RequestMethod::getMethod, Function.identity()))
            );

    public static RequestMethod find(String method) {
        if (!methods.containsKey(method)) {
            throw new IllegalArgumentException("유효하지 않은 method입니다.");
        }
        return methods.get(method);
    }

    public String getMethod() {
        return method;
    }
}
