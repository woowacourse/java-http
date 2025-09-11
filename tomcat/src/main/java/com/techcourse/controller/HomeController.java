package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseConfigurator;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        HttpResponseConfigurator.okWithPlainText(response, "Hello world!");
    }

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathEqualsTo("/");
    }
}
