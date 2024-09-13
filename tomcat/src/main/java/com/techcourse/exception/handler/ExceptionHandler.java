package com.techcourse.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;

public interface ExceptionHandler {

    boolean canHandle(Exception e);

    void handle(Exception e, HttpResponse response);
}
