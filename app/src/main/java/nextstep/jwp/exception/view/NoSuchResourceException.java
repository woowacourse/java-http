package nextstep.jwp.exception.view;

import nextstep.jwp.exception.ApplicationException;

public class NoSuchResourceException extends ApplicationException {

    private static final String MESSAGE = "해당하는 정적 파일이 없습니다.";
    private static final String HTTP_STATUS = "404";

    public NoSuchResourceException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public NoSuchResourceException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
