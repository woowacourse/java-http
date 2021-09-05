package nextstep.jwp.http.exception;

import nextstep.jwp.exception.BaseException;

public class HttpFormatException extends BaseException {
    private static final String MESSAGE = "HTTP 형식이 올바르지 않습니다.";

    public HttpFormatException() {
        super(MESSAGE);
    }
}
