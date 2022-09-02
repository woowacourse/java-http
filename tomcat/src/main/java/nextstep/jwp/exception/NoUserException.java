package nextstep.jwp.exception;

public class NoUserException extends RuntimeException {

    private static final String MESSAGE = "해당 유저가 없습니다.";

    public NoUserException() {
        super(MESSAGE);
    }
}
