package nextstep.jwp.http.request.request_line;

import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod from(String httpMethod) {
        return Arrays.stream(values())
            .filter(method -> Objects.equals(method.name(), httpMethod.toUpperCase()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find httpMethod"));
    }
}
