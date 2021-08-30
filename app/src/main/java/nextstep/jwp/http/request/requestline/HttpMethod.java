package nextstep.jwp.http.request.requestline;

import java.util.Arrays;
import nextstep.jwp.exception.NotImplementedException;

public enum HttpMethod {
    GET,
    POST;
    /*
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE;
    */

    public static HttpMethod matchOf(String requestMethod) {
        return Arrays.stream(values())
            .filter(method -> requestMethod.equals(method.name()))
            .findAny()
            .orElseThrow(NotImplementedException::new);
    }
}
