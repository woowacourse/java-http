package nextstep.jwp.service.exception;

import nextstep.jwp.exception.BaseException;

public class UserNotFoundException extends BaseException {
    private static final String MESSAGE = "유저를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
