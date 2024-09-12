package com.techcourse.exception;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    public void handle(Exception e, HttpResponse response) {
        if (e instanceof UnauthorizedException) {
            response.redirectTo("/401.html");
            return;
        }
        if (e instanceof IllegalArgumentException) {
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setTextBody(e.getMessage());
            return;
        }
        handleDefaultException(e, response);
    }

    private void handleDefaultException(Exception e, HttpResponse response) {
        log.error(e.getMessage(), e);
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setTextBody("서버에 문제가 발생헀습니다.");
    }
}
