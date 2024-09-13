package com.techcourse.exception.handler;

import com.techcourse.exception.UnauthorizedException;
import org.apache.coyote.http11.response.HttpResponse;

public class UnauthorizedExceptionHandler implements ExceptionHandler {

    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    public boolean canHandle(Exception e) {
        return e instanceof UnauthorizedException;
    }

    public void handle(Exception e, HttpResponse response) {
        response.redirectTo(UNAUTHORIZED_PAGE);
    }
}
