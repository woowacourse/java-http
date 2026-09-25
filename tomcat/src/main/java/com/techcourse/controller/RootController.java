package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        renderHtml(response, "Hello world!");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        doGet(request, response);
    }
}
