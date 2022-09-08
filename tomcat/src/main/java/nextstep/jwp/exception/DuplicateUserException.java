package nextstep.jwp.exception;

public class DuplicateUserException extends RuntimeException {

    private static final String MESSAGE = "이미 사용중인 아이디입니다.";

    public DuplicateUserException() {
        super(MESSAGE);
    }
}
