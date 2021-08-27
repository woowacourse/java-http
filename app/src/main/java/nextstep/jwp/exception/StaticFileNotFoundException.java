package nextstep.jwp.exception;

public class StaticFileNotFoundException extends CustomResponseException {
    public StaticFileNotFoundException(String message) {
        super(message);
    }
}
