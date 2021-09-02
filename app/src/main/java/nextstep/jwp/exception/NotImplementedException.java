package nextstep.jwp.exception;

public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
        super("요청한 메소드 또는 연산은 미구현된 것입니다.");
    }
}
