package com.techcourse.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class IllegalArgumentExceptionHandler implements ExceptionHandler {


    @Override
    public boolean canHandle(Exception e) {
        return e instanceof IllegalArgumentException;
    }

    public void handle(Exception e, HttpResponse response) {
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        response.setTextBody(e.getMessage());
    }
}
