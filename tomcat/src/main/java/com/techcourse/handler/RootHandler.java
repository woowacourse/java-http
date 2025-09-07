package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.OK;

import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.coyote.HttpRequestHandler;

public class RootHandler implements HttpRequestHandler {

    @Override
    public void handleGet(ServletRequest request, ServletResponse response) {
        response.setStatus(OK);
        response.setContentType("text/html;charset=utf-8");
        response.setBody("Hello world!");
    }

    @Override
    public void handlePost(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("POST 요청은 지원하지 않습니다.");
    }
}
