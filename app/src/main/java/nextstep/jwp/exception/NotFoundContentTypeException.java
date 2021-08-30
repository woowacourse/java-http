package nextstep.jwp.exception;

public class NotFoundContentTypeException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 Content Type 입니다.";

    public NotFoundContentTypeException() {
        super(MESSAGE);
    }
}
