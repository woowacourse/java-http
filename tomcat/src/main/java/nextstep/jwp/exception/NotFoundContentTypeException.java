package nextstep.jwp.exception;

public class NotFoundContentTypeException extends RuntimeException {

    public NotFoundContentTypeException() {
        super("존재하지 않는 Content Type 입니다.");
    }
}
