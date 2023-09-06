package nextstep.jwp.application.exception;

public class LoginFailureException extends IllegalArgumentException {

    public LoginFailureException() {
        super("로그인에 실패했습니다.");
    }
}
