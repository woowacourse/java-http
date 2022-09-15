package nextstep.jwp.exception;

public class CookieFormatException extends RuntimeException {

    public CookieFormatException() {
        super("HttpCookie 형식이 잘못됬습니다");
    }
}
