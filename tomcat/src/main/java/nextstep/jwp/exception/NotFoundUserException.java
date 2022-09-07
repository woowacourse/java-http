package nextstep.jwp.exception;

public class NotFoundUserException extends NotFoundException{

    public NotFoundUserException() {
        super("존재하지 않는 유저입니다.");
    }
}
