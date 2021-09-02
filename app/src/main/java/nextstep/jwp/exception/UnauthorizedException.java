package nextstep.jwp.exception;

public class UnauthorizedException extends RuntimeException {

    private static final String MESSAGE = "/401.html";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
