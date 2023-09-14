package nextstep.jwp.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException() {
    }

    public UncheckedServletException(Exception e) {
        super(e);
    }
}
