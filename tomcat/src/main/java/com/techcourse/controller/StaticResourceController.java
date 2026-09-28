package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final var path = request.getPath();
        if ("/index.html".equals(path)
                || "/401.html".equals(path)
                || "/css/styles.css".equals(path)
                || path.startsWith("/js/")
                || path.startsWith("/assets/")) {
            response.forward(path);
            return;
        }
        response.body("Hello world!");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        doGet(request, response);
    }
}
