package nextstep.jwp.exception.user;

import nextstep.jwp.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    private static final String MESSAGE = "해당 사용자를 찾을 수 없습니다.";
    private static final String HTTP_STATUS = "400";

    public UserNotFoundException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public UserNotFoundException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
