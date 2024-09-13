package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.message.request.HttpMethod;

public class HttpNotSupportedException extends RuntimeException {

    public HttpNotSupportedException(HttpMethod method, String urlPath) {
        super(String.format("%s %s 는 지원되지 않는 요청입니다.", method, urlPath));
    }
}
