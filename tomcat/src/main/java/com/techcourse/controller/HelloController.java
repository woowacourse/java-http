package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

// '/' 경로에 대한 처리
public class HelloController implements Controller {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return ResponseEntity.ok("Hello world!", "text/html;charset=utf-8");
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
