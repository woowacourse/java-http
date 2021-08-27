package nextstep.jwp.exception;

public class StaticFileNotFoundException extends RuntimeException {
    public StaticFileNotFoundException(String message) {
        super(message);
    }
}
