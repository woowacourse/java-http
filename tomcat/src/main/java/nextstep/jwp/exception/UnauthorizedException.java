package nextstep.jwp.exception;

public class UnauthorizedException extends RuntimeException {

    private static final String ERROR_MESSAGE = "로그인에 실패했습니다.";

    public UnauthorizedException() {
        super(ERROR_MESSAGE);
    }
}
