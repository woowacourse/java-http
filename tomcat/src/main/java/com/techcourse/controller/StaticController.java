package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.endsWith(".css")) {
            serveStaticFile(request.getPath(), response, "text/css;charset=utf-8");
            return;
        }

        if (request.endsWith(".html")) {
            serveStaticFile(request.getPath(), response, "text/html;charset=utf-8");
            return;
        }

        if (request.endsWith(".js")) {
            serveStaticFile(request.getPath(), response, "text/javascript;charset=utf-8");
            return;
        }

        final byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        response.setBody(body);
        response.send();
    }
}
