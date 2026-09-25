package com.techcourse.web.controller;

import com.techcourse.web.resource.StaticResourceHandler;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler;

    public StaticResourceController(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        staticResourceHandler.serve(request.getPathUri(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {

    }
}
