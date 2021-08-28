package nextstep.jwp.network;

import nextstep.jwp.network.exception.InvalidHttpMethodException;

import java.util.Arrays;

public enum HttpMethod {

    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT;

    public static HttpMethod of(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> name.equals(httpMethod.name()))
                .findFirst()
                .orElseThrow(() -> new InvalidHttpMethodException(name));
    }
}
