package nextstep.jwp.exception;

public class HTTPVersionNotSupportedException extends RuntimeException {

    public HTTPVersionNotSupportedException() {
        super("서버에서 지원하지 않는 HTTP 버전입니다.");
    }
}
