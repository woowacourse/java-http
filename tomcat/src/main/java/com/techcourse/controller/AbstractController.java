package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getMethod().isGet()) {
            doGet(request, response);
            return;
        }

        if (request.getMethod().isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        // NOOP
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        // NOOP
    }

    protected void redirect(final HttpResponse response, final String location) {
        redirect(response, 302, location);
    }

    protected void redirect(final HttpResponse response, final int statusCode, final String location) {
        response.setStatusCode(statusCode);
        response.setHeader("Location", location);
        response.setBody("");
    }

    protected void renderResource(final HttpResponse response, final String resourcePath)
            throws IOException {
        final var resource = getClass().getClassLoader()
                .getResource("static" + resourcePath);
        if (resource == null) {
            response.setStatusCode(404);
            response.setBody("");
            return;
        }

        final byte[] body;
        try {
            body = Files.readAllBytes(Path.of(resource.toURI()));
        } catch (URISyntaxException e) {
            throw new IOException("정적 리소스를 읽을 수 없습니다: " + resourcePath, e);
        }

        response.setStatusCode(200);
        response.setHeader("Content-Type", contentType(resourcePath));
        response.setBody(body);
    }

    protected void renderHtml(final HttpResponse response, final String body) {
        response.setStatusCode(200);
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(body);
    }

    private String contentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
