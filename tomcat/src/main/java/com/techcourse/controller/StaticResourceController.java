package com.techcourse.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StaticResourceController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler =
            new StaticResourceHandler();

    @Override
    protected void doGet(final HttpRequest request,
                         final HttpResponse response) throws IOException {
        final String requestPath = request.getPath();

        if ("/".equals(requestPath)) {
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8));
            return;
        }

        staticResourceHandler.serve("static" + requestPath, response);
    }
}
