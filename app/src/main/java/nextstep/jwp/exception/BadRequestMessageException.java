package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class BadRequestMessageException extends AbstractHttpException {

    private static final String MESSAGE = "{\"message\": \"잘못된 request message 입니다.\"}";
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BadRequestMessageException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
