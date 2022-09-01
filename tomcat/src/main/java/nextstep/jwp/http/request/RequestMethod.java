package nextstep.jwp.http.request;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidRequestMethodException;

public enum RequestMethod {

    GET,
    POST
    ;

    public static RequestMethod from(final String method) {
        return Arrays.stream(RequestMethod.values())
            .filter(value -> value.name().equals(method))
            .findAny()
            .orElseThrow(InvalidRequestMethodException::new);
    }
}
