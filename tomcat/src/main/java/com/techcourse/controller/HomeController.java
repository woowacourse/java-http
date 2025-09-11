package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeType;
import org.apache.coyote.http11.response.ResponseEntity;

// '/' 경로에 대한 처리
public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        return ResponseEntity.ok("Hello world!", MimeType.HTML);
    }

    @Override
    public String getPath() {
        return "/";
    }
}
