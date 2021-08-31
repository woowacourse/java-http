package nextstep.jwp.exception;

import nextstep.jwp.web.http.response.StatusCode;

public class ExistingUserException extends HttpException {

    private static final StatusCode CODE = StatusCode.INTERNAL_SERVER_ERROR;

    public ExistingUserException() {
        super(CODE);
    }
}
