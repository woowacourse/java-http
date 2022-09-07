package nextstep.jwp.exception;

public class NotFoundResourcePathException extends NotFoundException{

    public NotFoundResourcePathException() {
        super("존재하지 않는 리소스 경로입니다.");
    }
}
