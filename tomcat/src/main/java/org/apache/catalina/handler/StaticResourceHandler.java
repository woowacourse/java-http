package org.apache.catalina.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class StaticResourceHandler implements Handler {

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getPath();
        return ContentType.supports(path);
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final String path = request.getPath();

        final String resourcePath = "static" + path;
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource == null) {
            notFoundResponse(request, response);
            return;
        }

        byte[] body;
        if (ContentType.isBinary(resourcePath)) {
            body = Files.readAllBytes(new File(resource.getPath()).toPath());
        } else {
            body = Files.readString(new File(resource.getPath()).toPath(), StandardCharsets.UTF_8)
                    .getBytes(StandardCharsets.UTF_8);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.fromPath(resourcePath));
        headers.addHeader("Content-Length", String.valueOf(body.length));

        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.OK));
        response.setHeaders(headers);
        response.setBody(body);
    }

    private void notFoundResponse(final HttpRequest request, final HttpResponse response) throws IOException {
        final String resourcePath = "static/404.html";
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        final String body;
        if (resource != null) {
            body = Files.readString(new File(resource.getPath()).toPath(), StandardCharsets.UTF_8);
        } else {
            body = "<h1>404 Not Found</h1>";
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.NOT_FOUND));
        response.setHeaders(headers);
        response.setBody(body.getBytes(StandardCharsets.UTF_8));
    }
}
