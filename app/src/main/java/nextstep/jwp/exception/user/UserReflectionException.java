package nextstep.jwp.exception.user;

import nextstep.jwp.exception.ApplicationException;

public class UserReflectionException extends ApplicationException {

    private static final String MESSAGE = "사용자 리플렉션 과정에서 문제가 발생했습니다.";
    private static final String HTTP_STATUS = "500";

    public UserReflectionException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public UserReflectionException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
