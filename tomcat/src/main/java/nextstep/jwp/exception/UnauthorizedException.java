package nextstep.jwp.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("로그인에 실패했습니다.");
    }
}
