package com.techcourse.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public boolean canHandle(Exception e) {
        return true;
    }

    @Override
    public void handle(Exception e, HttpResponse response) {
        log.error(e.getMessage(), e);
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setTextBody("서버에 문제가 발생했습니다.");
    }
}
