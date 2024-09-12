package com.techcourse.controller;

import java.nio.file.Path;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11StatusCode;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private final Http11ResourceFinder resourceFinder = new Http11ResourceFinder();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Path path = resourceFinder.find(request.requestUri());
        response.setBodyAndContentType(path);

        if (path.endsWith("404.html")) {
            response.setStatusCode(Http11StatusCode.NOT_FOUND);
        }
    }
}
