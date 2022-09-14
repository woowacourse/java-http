package nextstep.jwp.exception;

public class MethodNotAllowedException extends RequestException {

    public MethodNotAllowedException() {
        super("지원하지 않는 메소드");
    }
}
