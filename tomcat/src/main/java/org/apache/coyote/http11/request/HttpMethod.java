package org.apache.coyote.http11.request;

import java.util.Arrays;
import nextstep.jwp.exception.NotSupportedHttpMethodException;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod findHttpMethod(String inputHttpMethod) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(inputHttpMethod))
                .findFirst()
                .orElseThrow(NotSupportedHttpMethodException::new);
    }
}
