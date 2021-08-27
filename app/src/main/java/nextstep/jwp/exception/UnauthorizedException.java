package nextstep.jwp.exception;

public class UnauthorizedException extends CustomResponseException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
