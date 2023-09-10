package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.http11.request.exception.HttpRequestException.MethodNotAllowed;

public enum HttpMethod {
    GET,
    POST,
    PATCH,
    PUT,
    DELETE;

    public static HttpMethod findHttpMethod(final String httpMethodName) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(httpMethodName))
                .findAny()
                .orElseThrow(MethodNotAllowed::new);
    }
}
