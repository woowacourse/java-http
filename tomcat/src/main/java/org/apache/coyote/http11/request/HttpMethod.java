package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.http11.BadRequestException;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("지원하지 않는 HTTP 메서드입니다: " + method));
    }
}
