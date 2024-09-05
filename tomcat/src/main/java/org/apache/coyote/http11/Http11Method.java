package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;

public enum Http11Method {

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

    public static void validate(String method) {
        try {
            valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP Method가 아닙니다."));
        }
    }

    public boolean equals(String method) {
        return this.name().equals(method);
    }
}
