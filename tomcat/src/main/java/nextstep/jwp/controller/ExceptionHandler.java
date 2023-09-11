package nextstep.jwp.controller;

import common.ResponseStatus;
import nextstep.jwp.exception.AuthException;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.response.HttpResponse;

public class ExceptionHandler {

    private static final String UNAUTHORIZED_URL = "/401.html";
    private static final String NOT_FOUND_URL = "/404.html";

    public void handle(HttpResponse httpResponse, Exception e) {
        if (e instanceof AuthException) {
            httpResponse.setResponseResource(ResponseStatus.UNAUTHORIZED, UNAUTHORIZED_URL);
            return;
        }
        if (e instanceof NotFoundException) {
            httpResponse.setResponseResource(ResponseStatus.NOT_FOUND, NOT_FOUND_URL);
        }
    }
}
