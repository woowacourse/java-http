package nextstep.jwp.exception;

public class NotFoundFileException extends UncheckedServletException {
    public NotFoundFileException(final String message) {
        super(message);
    }
}
