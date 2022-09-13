package org.apache.coyote.http11.exception;

public class NotFoundServletException extends UncheckedServletException {

    public NotFoundServletException() {
        super(new RuntimeException("요청을 처리할 서블릿이 존재하지 않습니다."));
    }
}
