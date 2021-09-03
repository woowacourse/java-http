package nextstep.jwp.http.exception;

import nextstep.jwp.exception.BaseException;

public class UnsupportedExtensionException extends BaseException {
    private static final String MESSAGE = "지원하지 않는 형식 입니다.";

    public UnsupportedExtensionException() {
        super(MESSAGE);
    }
}
