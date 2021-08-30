package nextstep.jwp.exception;

public class NotImplementedException extends RuntimeException {

    public NotImplementedException(String requestMethod) {
        super(String.format("%s 메서드를 지원하지 않습니다.", requestMethod));
    }
}
