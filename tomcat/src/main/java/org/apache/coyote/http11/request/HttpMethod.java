package org.apache.coyote.http11.request;

import java.util.Arrays;
import nextstep.jwp.exception.NotSupportedHttpMethodException;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod findHttpMethod(String inputHttpMethod) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.isSameMethod(inputHttpMethod))
                .findFirst()
                .orElseThrow(NotSupportedHttpMethodException::new);
    }

    private boolean isSameMethod(String inputHttpMethod) {
        return name().equals(inputHttpMethod);
    }
}
