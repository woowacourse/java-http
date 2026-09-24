package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        getSession(request, response);
        renderResource(response, "/404.html");
        response.setStatusCode(404);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        doGet(request, response);
    }
}
