package nextstep.jwp.exception;

public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException() {
        super("로그인에 실패했습니다.");
    }
}
