package nextstep.jwp.exception;

public class InvalidHttpVersionException extends RuntimeException {

    public InvalidHttpVersionException() {
        super("지원하지 않는 Http 버전입니다.");
    }
}
