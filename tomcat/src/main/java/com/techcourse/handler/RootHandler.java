package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.OK;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpResponse;

public class RootHandler implements HttpRequestHandler {

    @Override
    public void handleGet(HttpRequest request, HttpResponse response) {
        response.setStatus(OK);
        response.setContentType("text/html;charset=utf-8");
        response.setBody("Hello world!");
    }

    @Override
    public void handlePost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("POST 요청은 지원하지 않습니다.");
    }
}
