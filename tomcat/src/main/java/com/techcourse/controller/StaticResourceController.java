package com.techcourse.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        render(request.getPath(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        render(request.getPath(), response);
    }

    public void render(String path, HttpResponse response) throws IOException {
        if ("/".equals(path)) {
            response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8), "text/html;charset=utf-8");
            return;
        }

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            byte[] body = Objects.requireNonNull(resource).readAllBytes();
            response.setBody(body, resolveContentType(path));
        }
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
