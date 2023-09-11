package nextstep.jwp.exception;

public class NotFoundException extends UncheckedServletException {

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
