package org.apache.coyote.exception;

import static org.apache.coyote.response.Status.METHOD_NOT_ALLOWED;

import org.apache.coyote.request.Method;

public class MethodNotAllowedException extends HttpException {

    public MethodNotAllowedException(final String path, final Method method) {
        this.status = METHOD_NOT_ALLOWED;
        this.message = "요청하신 메소드를 사용할 수 없습니다 :" + method.toString() + " " + path;
    }

    public MethodNotAllowedException(final String method) {
        this.status = METHOD_NOT_ALLOWED;
        this.message = "요청하신 메소드를 사용할 수 없습니다 :" + method;
    }
}
