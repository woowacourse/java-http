package com.techcourse.controller;

import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class StaticResourceController extends AbstractController {

    private final String resourcePath;
    private final String contentType;

    public StaticResourceController(String resourcePath, String contentType) {
        this.resourcePath = resourcePath;
        this.contentType = contentType;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        render(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        render(response);
    }

    void render(HttpResponse response) throws IOException {
        try (var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
            }
            response.header("Content-Type", contentType);
            response.body(resource.readAllBytes());
        }
    }
}
