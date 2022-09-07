package nextstep.jwp.http;

public class InvalidCookieException extends RuntimeException {

    public InvalidCookieException() {
        super("올바른 쿠키 형태가 아닙니다.");
    }
}
