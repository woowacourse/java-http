package nextstep.jwp.service.exception;

import nextstep.jwp.exception.UnAuthorizedException;

public class UserPasswordInValidException extends UnAuthorizedException {
    private static final String MESSAGE = "패스워드가 일치 하지 않습니다.";

    public UserPasswordInValidException() {
        super(MESSAGE);
    }
}
