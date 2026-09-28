package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public final class IndexController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourceType = response.resourceType();
        response.setResponse(HttpResponse.ok(
            StaticResourceLoader.load("/index.html"), resourceType));
    }
}
