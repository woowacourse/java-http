package com.techcourse.controller;

import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setNotFound(request.getRequestLine().getProtocol());
    }
}
