package nextstep.jwp.web.exception;

public class UnsupportedContentType extends RuntimeException {
    private static final String MESSAGE = "지원되지 않는 Content Type입니다.";

    public UnsupportedContentType() {
        super(MESSAGE);
    }
}
