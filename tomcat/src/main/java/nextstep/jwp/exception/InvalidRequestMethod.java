package nextstep.jwp.exception;

public class InvalidRequestMethod extends RuntimeException {

    public InvalidRequestMethod() {
        super("존재하지 않는 요청 메서드입니다.");
    }
}
