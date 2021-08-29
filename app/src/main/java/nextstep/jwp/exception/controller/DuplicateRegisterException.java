package nextstep.jwp.exception.controller;

import nextstep.jwp.exception.ApplicationException;

public class DuplicateRegisterException extends ApplicationException {

    private static final String MESSAGE = "이미 회원가입되어 있습니다.";
    private static final String HTTP_STATUS = "400";

    public DuplicateRegisterException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public DuplicateRegisterException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
