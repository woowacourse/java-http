package nextstep.jwp.exception;

public class InvalidPasswordException extends RuntimeException {

    private static final String MESSAGE = "잘못된 비밀번호입니다.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }
}
