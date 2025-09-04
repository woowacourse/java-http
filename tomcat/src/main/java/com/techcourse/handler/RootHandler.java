package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.*;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpResponse;

public class RootHandler implements HttpRequestHandler {

    @Override
    public void handleGet(HttpRequest request, HttpResponse response) {
        response.setStatus(OK);
        response.setBody("Hello World");
        response.setContentType("text/plain;charset=utf-8");
    }
}
