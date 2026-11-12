package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public final class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourcePath = request.requestLine().path().resource();
        response.setResponse(HttpResponse.ok(
            StaticResourceLoader.load(resourcePath), resourceType(resourcePath)));
    }

    private String resourceType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "css";
        }
        return "html";
    }
}
