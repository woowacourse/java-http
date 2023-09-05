package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethod from(final String request) {
        return Arrays.stream(values())
                .filter(httpMethodType -> httpMethodType.name().equals(request))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("해당하는 HttpMethod가 존재하지 않습니다."));
    }
}
