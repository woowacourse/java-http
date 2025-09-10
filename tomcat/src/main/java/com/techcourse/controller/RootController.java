package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.value.HttpHeader;
import org.apache.coyote.http.value.StatusCode;

public class RootController extends AbstractController {

    public RootController() {
        super("/");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), "text/html;charset=utf-8");
        response.setBody("Hello world!");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }
}
