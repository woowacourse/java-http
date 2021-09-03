package nextstep.jwp.exception;

public class LoginException extends RuntimeException {

    private static final String MESSAGE = "잘못된 로그인입니다.";

    public LoginException() {
        super(MESSAGE);
    }
}
