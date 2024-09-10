package org.was.Controller.exception;

import org.apache.coyote.http11.HttpMethod;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException(HttpMethod method) {
        super("컨트롤러에서 지원하지 않는 메서드입니다. :" + method.name());
    }
}
