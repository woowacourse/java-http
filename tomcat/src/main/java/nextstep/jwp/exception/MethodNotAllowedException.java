package nextstep.jwp.exception;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException() {
        super("요청한 HTTP 메서드가 허용되지 않는 URL입니다.");
    }
}
