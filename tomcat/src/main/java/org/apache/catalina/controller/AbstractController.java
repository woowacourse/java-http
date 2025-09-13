package org.apache.catalina.controller;

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

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.equalMethod("GET")) {
            doGet(request, response);
        }

        if (request.equalMethod("POST")) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void redirect(
            final HttpRequest request,
            final HttpResponse response,
            final String location
    ) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Location", location);
        headers.addHeader("Content-Length", "0");

        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.FOUND));
        response.setHeaders(headers);
        response.setBody(new byte[0]);
    }

    protected void renderPage(
            final HttpRequest request,
            final HttpResponse response,
            final String resourcePath
    ) throws IOException {
        final byte[] body = readStaticResource(resourcePath);

        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(body.length));

        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.OK));
        response.setHeaders(headers);
        response.setBody(body);
    }

    protected byte[] readStaticResource(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (ContentType.isBinary(resourcePath)) {
            return Files.readAllBytes(new File(resource.getPath()).toPath());
        }

        return Files.readString(new File(resource.getPath()).toPath(), StandardCharsets.UTF_8)
                .getBytes(StandardCharsets.UTF_8);
    }
}
