package nextstep.jwp;

import java.util.Arrays;

public enum Method {

    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT;

    public static Method of(String name) {
        return Arrays.stream(values())
                .filter(method -> name.equals(method.name()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
