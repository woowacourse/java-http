package com.techcourse.controller;

import org.apache.catalina.core.controller.AbstractController;
import org.apache.tomcat.util.http.request.HttpRequest;
import org.apache.tomcat.util.http.response.HttpResponse;
import org.apache.tomcat.util.http.value.HttpHeader;
import org.apache.tomcat.util.http.value.StatusCode;

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
}
