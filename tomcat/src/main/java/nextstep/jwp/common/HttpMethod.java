package nextstep.jwp.common;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidHttpMethodException;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod of(final String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findAny()
                .orElseThrow(InvalidHttpMethodException::new);
    }
}
