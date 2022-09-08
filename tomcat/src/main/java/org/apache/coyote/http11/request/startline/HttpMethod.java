package org.apache.coyote.http11.request.startline;

import java.util.Arrays;
import org.apache.coyote.http11.exception.MethodNotAllowedException;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    OPTIONS;

    public static HttpMethod from(String request) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(request))
                .findAny()
                .orElseThrow(MethodNotAllowedException::new);
    }
}
