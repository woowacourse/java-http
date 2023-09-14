package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.request.HttpMethod;

public class UnSupportedHttpMethodException extends RuntimeException {

    public UnSupportedHttpMethodException(final HttpMethod method) {
        super("컨트롤러가 처리할 수 없는 메서드입니다. method : " + method.name());
    }
}
