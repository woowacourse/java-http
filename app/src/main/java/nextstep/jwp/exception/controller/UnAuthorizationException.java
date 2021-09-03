package nextstep.jwp.exception.controller;

import nextstep.jwp.exception.ApplicationException;

public class UnAuthorizationException extends ApplicationException {

    private static final String MESSAGE = "접근 권한이 없습니다.";
    private static final String HTTP_STATUS = "401";

    public UnAuthorizationException() {
        this(MESSAGE, HTTP_STATUS);
    }

    private UnAuthorizationException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
