package nextstep.jwp.exception;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException() {
        super("유저를 찾을 수 없습니다.");
    }
}
