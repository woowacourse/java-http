package nextstep.jwp.service.exception;

import nextstep.jwp.exception.BaseException;

public class UserPasswordInValidException extends BaseException {
    private static final String MESSAGE = "패스워드가 일치 하지 않습니다.";

    public UserPasswordInValidException() {
        super(MESSAGE);
    }
}
