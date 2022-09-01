package nextstep.jwp.exception;

public class NotFoundHttpMethodException extends UncheckedServletException {
    public NotFoundHttpMethodException(final String message) {
        super(message);
    }
}
