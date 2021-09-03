package nextstep.jwp.exception;

public class NotfoundCookieException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 cookie 입니다. cookie name: ";

    public NotfoundCookieException(String cookieKey) {
        super(MESSAGE + cookieKey);
    }
}
