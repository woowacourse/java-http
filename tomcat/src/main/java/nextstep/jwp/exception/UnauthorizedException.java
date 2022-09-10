package nextstep.jwp.exception;

public class UnauthorizedException extends RequestException {

    public UnauthorizedException() {
        super("접근 권한이 존재하지 않습니다.");
    }
}
