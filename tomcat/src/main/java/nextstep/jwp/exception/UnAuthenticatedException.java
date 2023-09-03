package nextstep.jwp.exception;

public class UnAuthenticatedException extends RuntimeException {

    public UnAuthenticatedException() {
        super("인증에 실패하였습니다.");
    }
}
