package com.techcourse.controller;

import com.techcourse.view.ResourceRenderer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {
    private final ResourceRenderer resourceRenderer;

    public StaticResourceController(ResourceRenderer resourceRenderer) {
        this.resourceRenderer = resourceRenderer;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        render(request.getPath(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        render(request.getPath(), response);
    }

    private void render(String path, HttpResponse response) throws IOException {
        if ("/".equals(path)) {
            response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8), "text/html;charset=utf-8");
            return;
        }

        resourceRenderer.render(path, response);
    }
}
