package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.error.HttpException;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod pick(final String name) {
        return Arrays.stream(values())
            .filter(httpMethod -> httpMethod.name().equals(name))
            .findFirst()
            .orElseThrow(() ->
                new HttpException(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 http method 입니다."));
    }
}
