package nextstep.jwp.exception;

public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException() {
        super("유저를 조회할 수 없습니다.");
    }
}
