package nextstep.jwp.exception;

public class LoginFailedException extends IllegalArgumentException {

    public LoginFailedException() {
        super("로그인에 실패했습니다.");
    }
}
