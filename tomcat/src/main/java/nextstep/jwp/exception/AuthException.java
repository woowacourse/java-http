package nextstep.jwp.exception;

public class AuthException extends UncheckedServletException {

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }
}
