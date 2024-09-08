package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;

public enum HttpMethod {
    GET,
    POST,
    ;

    public static HttpMethod from(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new UncheckedServletException(e);
        }
    }
}
