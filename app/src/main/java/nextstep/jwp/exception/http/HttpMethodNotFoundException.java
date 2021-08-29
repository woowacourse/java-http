package nextstep.jwp.exception.http;

import nextstep.jwp.exception.ApplicationException;

public class HttpMethodNotFoundException extends ApplicationException {

    private static final String MESSAGE = "존재하지 않는 요청 형식입니다.";
    private static final String HTTP_STATUS = "400";

    public HttpMethodNotFoundException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public HttpMethodNotFoundException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
