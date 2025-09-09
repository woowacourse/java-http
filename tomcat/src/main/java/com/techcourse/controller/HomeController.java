package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

// '/' 경로에 대한 처리
public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setHttpResponse(
                ResponseEntity.ok("Hello world!", "text/html;charset=utf-8")
        );
    }

    @Override
    public String getPath() {
        return "/";
    }
}
