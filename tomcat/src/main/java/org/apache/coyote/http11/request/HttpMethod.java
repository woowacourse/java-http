package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.http11.request.exception.HttpMethodNotAllowedException;

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
                .orElseThrow(() -> new HttpMethodNotAllowedException("허용되지 않는 HTTP Method입니다."));
    }
}
