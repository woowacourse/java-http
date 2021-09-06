package nextstep.jwp.controller;

import nextstep.jwp.exception.AbstractHttpException;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.Response;

public class ControllerAdvice {

    private ControllerAdvice() {}

    public static void errorHandle(AbstractHttpException exception, Response response) {
        HttpStatus httpStatus = exception.httpStatus();
        if (httpStatus.equals(HttpStatus.BAD_REQUEST) || httpStatus.equals(HttpStatus.CONFLICT)
            || httpStatus.equals(HttpStatus.METHOD_NOT_ALLOWED)) {
            response.setErrorResponse(exception.getMessage(), httpStatus);
            return;
        }
        if (httpStatus.equals(HttpStatus.UNAUTHORIZED) || httpStatus.equals(HttpStatus.NOT_FOUND)) {
            response.set302Found(exception.getMessage());
        }
    }
}
