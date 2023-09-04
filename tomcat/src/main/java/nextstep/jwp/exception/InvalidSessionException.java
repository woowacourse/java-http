package nextstep.jwp.exception;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
        super("세션이 유효하지 않습니다.");
    }
}
