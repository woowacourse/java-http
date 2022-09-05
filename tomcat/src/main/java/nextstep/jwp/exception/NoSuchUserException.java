package nextstep.jwp.exception;

public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException(final String message) {
        super(message);
    }

    public NoSuchUserException() {
        this("존재하지 않는 user 입니다.");
    }
}
