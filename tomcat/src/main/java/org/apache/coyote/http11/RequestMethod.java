package org.apache.coyote.http11;

import java.util.Arrays;
import nextstep.jwp.exception.MethodNotAllowedException;

public enum RequestMethod {

    GET,
    POST;

    public static RequestMethod find(String requestMethod) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(requestMethod))
                .findAny()
                .orElseThrow(MethodNotAllowedException::new);
    }
}
