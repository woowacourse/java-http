package org.apache.catalina.servlet;

import nextstep.jwp.exception.UncheckedServletException;

public class NotFoundServletException extends UncheckedServletException {

    public NotFoundServletException() {
        super(new RuntimeException("요청을 처리할 서블릿이 존재하지 않습니다."));
    }
}
