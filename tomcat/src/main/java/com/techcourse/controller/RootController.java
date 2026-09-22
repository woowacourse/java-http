package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.MappedController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController implements MappedController {

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        response.setBody("Hello world!", "text/html;charset=utf-8");
        return response;
    }
}
