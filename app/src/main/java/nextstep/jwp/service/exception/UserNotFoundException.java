package nextstep.jwp.service.exception;

import nextstep.jwp.exception.UnAuthorizedException;

public class UserNotFoundException extends UnAuthorizedException {
    private static final String MESSAGE = "유저를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
