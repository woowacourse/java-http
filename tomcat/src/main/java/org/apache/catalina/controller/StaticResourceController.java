package org.apache.catalina.controller;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean supports(final HttpRequest request) {
        return request.getRequestLine().getMethod().equals("GET");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String requestPath = request.getRequestLine().getPath();
        final String resourcePath = "static" + requestPath;
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource != null) {
            final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));
            final String contentType = getContentType(resourcePath);
            response.putHeader("Content-Type", contentType);

            response.setStatusLine("HTTP/1.1 200 OK");
            response.setBody(body);
        } else {
            handleNotFound(response);
        }
    }

    private void handleNotFound(final HttpResponse response) throws Exception {
        final URL resource404 = getClass().getClassLoader().getResource("static/404.html");
        final byte[] body;
        if (resource404 != null) {
            body = Files.readAllBytes(Path.of(resource404.toURI()));
        } else {
            body = "404 Not Found".getBytes(StandardCharsets.UTF_8);
        }

        response.setStatusLine("HTTP/1.1 404 Not Found");
        response.setBody(body);
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }
}
