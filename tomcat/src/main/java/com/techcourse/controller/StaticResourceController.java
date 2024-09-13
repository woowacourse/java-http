package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.getStaticResource(request.getUrl());
    }
}
