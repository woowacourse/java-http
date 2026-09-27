package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
            return;
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
            return;
        }

        throw new IllegalArgumentException(
                "지원하지 않는 HTTP 메서드입니다: " + request.getMethod()
        );
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }

    protected void renderStaticResource(final HttpResponse response, final String path) throws IOException {
        final var resourcePath = "static" + path;
        final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다." + resourcePath);
        }

        try (resource) {
            final var body = resource.readAllBytes();
            writeOk(response, body, determineContentType(path));
        }
    }

    protected void writeOk(final HttpResponse response, final byte[] body, final String contentType) {
        response.setStatus(200, "OK");
        response.setBody(body);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(body.length));
    }

    protected void redirect(final HttpResponse response, final String location) {
        response.setStatus(302, "Found");
        response.setBody(new byte[0]);
        response.addHeader("Location", location);
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
