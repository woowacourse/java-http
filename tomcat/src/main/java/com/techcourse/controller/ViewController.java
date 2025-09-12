package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResponseHandler;

public class ViewController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ResponseHandler.sendDefaultResource(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }
}
