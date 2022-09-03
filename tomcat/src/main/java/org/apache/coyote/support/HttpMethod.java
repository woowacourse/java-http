package org.apache.coyote.support;

import org.apache.coyote.exception.HttpException;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod of(String method) {
        try {
            return valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }
}
