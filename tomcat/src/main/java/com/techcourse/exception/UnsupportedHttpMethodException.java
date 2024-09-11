package com.techcourse.exception;

import org.apache.coyote.http11.request.startLine.HttpMethod;

public class UnsupportedHttpMethodException extends RuntimeException {

    public UnsupportedHttpMethodException(HttpMethod httpMethod) {
        super("지원하지 않는 HTTP 메소드: " + httpMethod);
    }
}
