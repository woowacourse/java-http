package nextstep.jwp.exception;

public class NotAllowedMethodException extends RuntimeException {

    public NotAllowedMethodException() {
        super("해당하는 Method가 존재하지 않습니다.");
    }
}
