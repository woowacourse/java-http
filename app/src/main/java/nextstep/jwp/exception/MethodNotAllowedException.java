package nextstep.jwp.exception;

public class MethodNotAllowedException extends Exception {

    public MethodNotAllowedException() {
        super("잘못된 요청 방식입니다.");
    }
}
