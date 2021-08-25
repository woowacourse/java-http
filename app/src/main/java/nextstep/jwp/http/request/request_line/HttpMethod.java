package nextstep.jwp.http.request.request_line;

import java.util.Arrays;
import java.util.Objects;
import nextstep.jwp.http.exception.InternalServerException;

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
