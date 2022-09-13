package customservlet.exception;

import org.apache.coyote.http11.exception.NotFoundException;

public class NotFoundHandlerException extends NotFoundException {
    public NotFoundHandlerException() {
        super("지원하는 핸들러가 없습니다.");
    }
}
