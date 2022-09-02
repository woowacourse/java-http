package org.apache.coyote.http11.request;

import java.util.Arrays;
import nextstep.jwp.exception.UncheckedServletException;

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
                .orElseThrow(() -> new UncheckedServletException("올바른 Http Method가 아닙니다."));
    }
}
