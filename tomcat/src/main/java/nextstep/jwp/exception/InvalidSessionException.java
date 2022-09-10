package nextstep.jwp.exception;

public class InvalidSessionException extends BadRequestException {

    public InvalidSessionException() {
        super("유효하지 않은 Session 아이디입니다.");
    }
}
