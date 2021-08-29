package nextstep.jwp.exception;

public class UnauthorizedException extends RuntimeException {

    private static final String MESSAGE = "허용되지 않은 유저입니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }
}
