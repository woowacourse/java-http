package nextstep.jwp.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException(Exception e) {
        super(e);
    }

    public UncheckedServletException(final String message) {
        super(message);
    }
}
