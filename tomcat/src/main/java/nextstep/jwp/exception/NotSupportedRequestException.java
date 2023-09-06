package nextstep.jwp.exception;

public class NotSupportedRequestException extends RuntimeException {

    public NotSupportedRequestException() {
        super("지원하는 핸들러가 존재하지 않습니다");
    }
}
