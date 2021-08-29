package nextstep.jwp.exception;

public class UsernameConflictException extends RuntimeException {

    private static final String MESSAGE = "{\"message\": \"이미 존재하는 아이디 입니다.\"}";

    public UsernameConflictException() {
        super(MESSAGE);
    }
}
