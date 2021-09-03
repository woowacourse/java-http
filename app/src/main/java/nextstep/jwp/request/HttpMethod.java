package nextstep.jwp.request;

import nextstep.jwp.exception.NoSupportedMethodException;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethod of(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> method.equalsIgnoreCase(httpMethod.name()))
                .findAny()
                .orElseThrow(() -> new NoSupportedMethodException("지원하지 않는 HttpMethod 입니다."));
    }
}
