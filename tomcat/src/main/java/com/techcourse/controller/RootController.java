package com.techcourse.controller;

import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setRoot(request.getRequestLine().getProtocol());
    }
}
