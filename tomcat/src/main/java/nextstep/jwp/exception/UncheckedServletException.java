package nextstep.jwp.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException() {
    }

    public UncheckedServletException(Exception e) {
        super(e);
    }

    public UncheckedServletException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
