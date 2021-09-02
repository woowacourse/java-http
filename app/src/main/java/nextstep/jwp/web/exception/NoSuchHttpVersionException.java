package nextstep.jwp.web.exception;

public class NoSuchHttpVersionException extends RuntimeException {
    private static final String MESSAGE = "HTTP 버전 정보를 찾을 수 없습니다.";

    public NoSuchHttpVersionException() {
        super(MESSAGE);
    }
}
