package nextstep.jwp.exception;

public class InvalidLoginRequestException extends RuntimeException {

    public InvalidLoginRequestException() {
        super("잘못된 로그인 요청입니다.");
    }
}
