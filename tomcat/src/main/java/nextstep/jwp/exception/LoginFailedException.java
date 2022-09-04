package nextstep.jwp.exception;

public class LoginFailedException extends RuntimeException {

    private static final String MESSAGE = "로그인에 실패했습니다.";

    public LoginFailedException() {
        super(MESSAGE);
    }
}
