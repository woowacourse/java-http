package nextstep.jwp.web;

import nextstep.jwp.web.exception.NoSuchHttpMethodException;

import java.util.Arrays;

public enum HttpMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static HttpMethod of(String input) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(input.toUpperCase()))
                .findFirst()
                .orElseThrow(NoSuchHttpMethodException::new);
    }
}
