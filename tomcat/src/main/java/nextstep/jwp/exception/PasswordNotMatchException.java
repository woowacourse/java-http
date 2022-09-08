package nextstep.jwp.exception;

public class PasswordNotMatchException extends RuntimeException {

    private static final String MESSAGE = "아이디나 비밀번호가 틀렸습니다";

    public PasswordNotMatchException() {
        super(MESSAGE);
    }
}
