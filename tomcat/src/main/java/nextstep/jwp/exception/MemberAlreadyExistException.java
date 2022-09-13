package nextstep.jwp.exception;

public class MemberAlreadyExistException extends RuntimeException {

    public MemberAlreadyExistException(final String message) {
        super(message);
    }
}
