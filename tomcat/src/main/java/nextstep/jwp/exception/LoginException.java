package nextstep.jwp.exception;

public class LoginException extends RuntimeException {

    public LoginException() {
        super("로그인에 실패하였습니다.");
    }
}
