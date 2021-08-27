package nextstep.jwp;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod of(String httpMethod) {
        return Arrays.stream(HttpMethod.values())
            .filter(it -> it.name().equals(httpMethod))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }
}
