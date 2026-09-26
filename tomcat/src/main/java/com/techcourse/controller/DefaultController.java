package com.techcourse.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.path().endsWith(".js")) {
            new StaticResourceController("static" + request.path(), "text/javascript;charset=utf-8")
                    .render(response);
            return;
        }
        response.header("Content-Type", "text/html;charset=utf-8");
        response.body("Hello world!".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        doGet(request, response);
    }
}
