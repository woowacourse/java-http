package nextstep.jwp.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("해당 리소스에 유효한 인증 자격이 없습니다.");
    }
}
