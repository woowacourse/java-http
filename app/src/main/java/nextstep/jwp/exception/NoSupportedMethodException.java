package nextstep.jwp.exception;

public class NoSupportedMethodException extends RuntimeException {
    public NoSupportedMethodException() {
    }

    public NoSupportedMethodException(String message) {
        super(message);
    }

    public NoSupportedMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
