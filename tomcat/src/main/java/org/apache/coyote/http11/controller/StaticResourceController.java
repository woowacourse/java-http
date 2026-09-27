package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        serveResource(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        serveResource(request, response);
    }

    private void serveResource(HttpRequest request, HttpResponse response) throws IOException {
        String requestPath = extractPath(request.getRequestLine().getPath());
        if ("/".equals(requestPath)) {
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody("Hello world!");
            return;
        }

        String resourcePath = resolveResourcePath(requestPath);
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            response.notFound();
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody(readResource("static/404.html"));
            return;
        }

        response.addHeader("Content-Type", resolveContentType(resourcePath));
        response.setBody(readResource(resource));
    }

    private String extractPath(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf('?');
        if (queryStartIndex < 0) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryStartIndex);
    }

    private String resolveResourcePath(String requestPath) {
        if (!requestPath.contains(".")) {
            requestPath += ".html";
        }
        return "static" + requestPath;
    }

    private String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private String readResource(URL resource) throws IOException {
        try (var inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String readResource(String resourcePath) throws IOException {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("resource not found: " + resourcePath);
        }
        return readResource(resource);
    }
}
