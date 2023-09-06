package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundHttpMethodException;

import java.util.Arrays;

public enum HttpMethod {

    GET;

    public static HttpMethod findBy(final String method) {
        return Arrays.stream(values())
                     .filter(value -> value.name().equals(method))
                     .findFirst()
                     .orElseThrow(() -> new NotFoundHttpMethodException("해당 요청에 대한 메서드를 찾지 못했습니다."));
    }
}
