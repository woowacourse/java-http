package nextstep.jwp.exception;

public class NotFoundHandlerException extends RuntimeException {

    private static final String MESSAGE = "요청을 수행할 Handler를 찾지못했습니다.";

    public NotFoundHandlerException() {
        super(MESSAGE);
    }
}
