package nextstep.jwp.exception;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
        super("유효한 세션이 아닙니다.");
    }
}
