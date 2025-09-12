package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestUrl()
                .equals("/");
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        httpResponse.ok()
                .write("Hello world!");
    }
}
