package nextstep.jwp.exception;

public class NotFoundControllerException extends NotFoundException {

    public NotFoundControllerException() {
        super("존재하지 않는 API 입니다.");
    }
}
