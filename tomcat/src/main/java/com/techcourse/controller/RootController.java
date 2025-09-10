package com.techcourse.controller;

import org.apache.catalina.container.controller.AbstractController;
import org.apache.catalina.container.http.request.HttpRequest;
import org.apache.catalina.container.http.response.HttpResponse;
import org.apache.catalina.container.http.value.HttpHeader;
import org.apache.catalina.container.http.value.StatusCode;

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
