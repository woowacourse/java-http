package org.apache.coyote.http11.request.uri;

import org.apache.coyote.http11.handler.exception.HttpMethodInvalidException;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTION;

    public static HttpMethod from(final String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findAny()
                .orElseThrow(() -> new HttpMethodInvalidException(name));
    }
}
