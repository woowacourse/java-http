package com.techcourse.exception;

import org.apache.catalina.controller.ExceptionHandler;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse handle(Exception e) {
        if (e instanceof UnauthorizedException) {
            return HttpResponse.createRedirectResponse(HttpStatus.FOUND, "/401.html");
        }
        if (e instanceof IllegalArgumentException) {
            return HttpResponse.createTextResponse(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        log.error(e.getMessage(), e);
        return HttpResponse.createTextResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다.");
    }
}
