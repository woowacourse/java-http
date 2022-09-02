package nextstep.jwp.exception;

public class LoginException extends RuntimeException{

    private static final String MESSAGE = "로그인에 실패했습니다.";

    public LoginException() {
        super(MESSAGE);
    }
}
