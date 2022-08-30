package nextstep.jwp.exception;

public class NotFoundMethodException extends RuntimeException {

    public NotFoundMethodException() {
        super("잘못된 http method입니다.");
    }
}
