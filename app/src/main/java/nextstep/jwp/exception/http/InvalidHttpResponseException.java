package nextstep.jwp.exception.http;

import nextstep.jwp.exception.ApplicationException;

public class InvalidHttpResponseException extends ApplicationException {

    private static final String MESSAGE = "유효하지 않은 HTTP 응답입니다.";
    private static final String HTTP_STATUS = "500";

    public InvalidHttpResponseException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public InvalidHttpResponseException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
