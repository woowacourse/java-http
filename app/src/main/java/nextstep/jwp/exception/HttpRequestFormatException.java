package nextstep.jwp.exception;

public class HttpRequestFormatException extends RuntimeException {
    private static final String MSG = "HTTP 요청 포맷 에러 - 500";

    public HttpRequestFormatException() {
        super(MSG);
    }
}
