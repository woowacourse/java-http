package nextstep.jwp.exception;

import nextstep.jwp.web.http.response.StatusCode;

public class NoMatchingHandlerException extends HttpException {

    private static final StatusCode CODE = StatusCode.NOT_FOUND;

    public NoMatchingHandlerException() {
        super(CODE);
    }
}
