package nextstep.jwp.exception;

public class NotFoundContentTypeException extends UncheckedServletException {
    public NotFoundContentTypeException(final String message) {
        super(message);
    }
}
