package nextstep.jwp.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
