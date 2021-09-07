package nextstep.jwp.exception;

public class InvalidHttpSessionException extends RuntimeException {
    public InvalidHttpSessionException() {
    }

    public InvalidHttpSessionException(String message) {
        super(message);
    }
}
