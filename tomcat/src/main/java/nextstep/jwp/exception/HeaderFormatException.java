package nextstep.jwp.exception;

public class HeaderFormatException extends RuntimeException {

    public HeaderFormatException() {
        super("HttpHeader 형식이 잘못됬습니다");
    }
}
