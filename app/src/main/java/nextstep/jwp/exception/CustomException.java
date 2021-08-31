package nextstep.jwp.exception;

public abstract class CustomException extends RuntimeException {

    protected CustomException() {
    }

    protected CustomException(String message) {
        super(message);
    }
}
