package com.techcourse.controller;

import org.apache.catalina.controller.AbstractApiController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseConfigurator;

public class HomeController extends AbstractApiController {

    public HomeController() {
        super("/");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        HttpResponseConfigurator.okWithPlainText(response, "Hello world!");
    }
}
