package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class MethodNotAllowedException extends HttpStatusException {

    public MethodNotAllowedException() {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, "요청 메서드를 찾을 수 없습니다.");
    }
}
