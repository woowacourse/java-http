package nextstep.jwp.http.request;

import java.util.Arrays;
import nextstep.jwp.exception.NotImplementedException;

public enum Method {
    GET,
    POST;
    /*
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE;
     */

    public static Method matchOf(String requestMethod) {
        return Arrays.stream(values())
            .filter(method -> requestMethod.equals(method.name()))
            .findAny()
            .orElseThrow(NotImplementedException::new);
    }
}
