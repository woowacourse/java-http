package nextstep.jwp.exception;

public class NotFoundHandlerException extends NotFoundException {
    public NotFoundHandlerException() {
        super("지원하는 핸들러가 없습니다.");
    }
}
