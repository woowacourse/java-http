package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.resource.StaticResourceReader;

public class StaticResourceController extends AbstractController {

    private final StaticResourceReader staticResourceReader;

    public StaticResourceController(StaticResourceReader staticResourceReader) {
        this.staticResourceReader = staticResourceReader;
    }

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
        var resourceContent = staticResourceReader.read(resourcePath);
        if (resourceContent.isEmpty()) {
            response.notFound();
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody(staticResourceReader.read("static/404.html")
                    .orElseThrow(() -> new IllegalStateException("resource not found: static/404.html")));
            return;
        }

        response.addHeader("Content-Type", resolveContentType(resourcePath));
        response.setBody(resourceContent.get());
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
}
