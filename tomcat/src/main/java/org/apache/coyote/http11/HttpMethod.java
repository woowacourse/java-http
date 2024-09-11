package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;

public enum HttpMethod {

    GET,
    POST,
    HEAD,
    PUT,
    PATCH,
    DELETE,
    CONNECT,
    TRACE,
    OPTIONS,
    ;

    public static HttpMethod from(String method) {
        try {
            return valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP Method가 아닙니다."));
        }
    }

    public boolean isGet() {
        return this == GET;
    }


    public boolean isPost() {
        return this == POST;
    }
}
