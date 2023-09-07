package nextstep.jwp.exception;

public class NotAllowedMethodException extends RuntimeException {

    public NotAllowedMethodException() {
        super("해당하는 HttpMethod가 존재하지 않습니다.");
    }
}
