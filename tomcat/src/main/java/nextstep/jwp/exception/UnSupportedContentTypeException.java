package nextstep.jwp.exception;

public class UnSupportedContentTypeException extends RuntimeException {

    public UnSupportedContentTypeException() {
        super("지원하지 않는 Content-Type 입니다.");
    }
}
