package nextstep.jwp.exception;

public class AuthException extends RuntimeException {

    public AuthException() {
        super("아이디 혹은 비밀번호가 일치하지 않습니다.");
    }
}
