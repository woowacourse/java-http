package nextstep.jwp.exception.http.response;

import nextstep.jwp.exception.ApplicationException;

public class ContentTypeNotFoundException extends ApplicationException {

    private static final String MESSAGE = "존재하지 않는 Content-Type입니다.";
    private static final String HTTP_STATUS = "400";

    public ContentTypeNotFoundException() {
        this(MESSAGE, HTTP_STATUS);
    }

    public ContentTypeNotFoundException(String message, String httpStatus) {
        super(message, httpStatus);
    }
}
