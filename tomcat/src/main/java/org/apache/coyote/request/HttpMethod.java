package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH,
    ;

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
                .filter(method -> name.equals(method.name()))
                .findFirst()
                .orElseThrow(() -> new UncheckedServletException("올바르지 않은 HTTP Method입니다."));
    }
}
