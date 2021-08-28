package nextstep.jwp.exception.user;

import nextstep.jwp.exception.ApplicationException;

public class OutOfUserIdException extends ApplicationException {

    private static final String MESSAGE = "사용자를 더 이상 생성할 수 없습니다.";
    private static final String HTTP_STATUS = "500";

    public OutOfUserIdException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public OutOfUserIdException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
