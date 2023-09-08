package nextstep.jwp.exception;

public class UnsupportedMethodException extends RuntimeException {

    public UnsupportedMethodException() {
        super("요청 메서드가 잘못 되었습니다.");
    }
}
