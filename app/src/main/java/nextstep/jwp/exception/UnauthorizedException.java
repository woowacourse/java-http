package nextstep.jwp.exception;

public class UnauthorizedException extends RuntimeException {
    private static final String MSG = "인증 에러 - 401";

    public UnauthorizedException() {
        super(MSG);
    }
}
