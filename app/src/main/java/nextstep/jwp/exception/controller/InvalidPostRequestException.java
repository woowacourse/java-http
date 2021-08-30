package nextstep.jwp.exception.controller;

import nextstep.jwp.exception.ApplicationException;

public class InvalidPostRequestException extends ApplicationException {

    private static final String MESSAGE = "해당 컨트롤러에는 POST 요청을 보낼 수 없습니다.";
    private static final String HTTP_STATUS = "400";

    public InvalidPostRequestException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public InvalidPostRequestException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
