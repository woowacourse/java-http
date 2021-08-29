package nextstep.jwp.exception.http.request;

import nextstep.jwp.exception.ApplicationException;

public class InvalidHttpRequestException extends ApplicationException {

    private static final String MESSAGE = "유효하지 않은 HTTP 요청입니다.";
    private static final String HTTP_STATUS = "400";

    public InvalidHttpRequestException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public InvalidHttpRequestException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
