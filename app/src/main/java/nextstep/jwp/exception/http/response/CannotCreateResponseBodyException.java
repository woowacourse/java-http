package nextstep.jwp.exception.http.response;

import nextstep.jwp.exception.ApplicationException;

public class CannotCreateResponseBodyException extends ApplicationException {

    private static final String MESSAGE = "Response Body 생성 과정에 문제가 발생했습니다.";
    private static final String HTTP_STATUS = "500";

    public CannotCreateResponseBodyException() {
        this(MESSAGE, HTTP_STATUS);
    }

    private CannotCreateResponseBodyException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
