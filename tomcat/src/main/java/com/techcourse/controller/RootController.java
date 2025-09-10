package com.techcourse.controller;

import org.apache.controller.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.value.HttpHeader;
import org.apache.http.value.StatusCode;

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
