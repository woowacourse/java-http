package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    public String providableUrl() {
        return "/";
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setOk("Hello world!");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.setInternalServerError();
    }
}
