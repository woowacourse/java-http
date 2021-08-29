package nextstep.jwp.http.message.request.request_line;

import nextstep.jwp.http.exception.InternalServerException;

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
            .orElseThrow(InternalServerException::new);
    }
}
