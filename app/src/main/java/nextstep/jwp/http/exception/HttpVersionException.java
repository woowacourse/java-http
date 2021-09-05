package nextstep.jwp.http.exception;

import nextstep.jwp.exception.BaseException;

public class HttpVersionException extends BaseException {
    private static final String MESSAGE = "지원하지 않는 HTTP Version 입니다.";
    public HttpVersionException() {
        super(MESSAGE);
    }
}
