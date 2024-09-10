package org.apache.catalina.controller;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public HttpResponse handle(Exception e) {
        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
