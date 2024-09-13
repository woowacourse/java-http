package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends AbstractController {
    private static final String RESPONSE_CONTENT = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendTextFiles(RESPONSE_CONTENT);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }
}
