package org.apache.coyote.http11.handler;

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

    private static final String DEFAULT_PATH = "/";
    private static final String DEFAULT_BODY = "Hello world!";

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getPath();
        return path.equals(DEFAULT_PATH) || path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js");
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        final String path = request.getPath();

        if (path.equals(DEFAULT_PATH)) {
            return defaultResponse(request);
        }

        final String resourcePath = "static" + path;
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource == null) {
            return notFoundResponse(request);
        }

        final String body = Files.readString(new File(resource.getPath()).toPath(), StandardCharsets.UTF_8);
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.fromPath(path));
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return new HttpResponse(
                new StatusLine(request.getVersion(), StatusCode.OK),
                headers,
                body.getBytes(StandardCharsets.UTF_8)
        );
    }

    private HttpResponse defaultResponse(final HttpRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(DEFAULT_BODY.getBytes(StandardCharsets.UTF_8).length));

        return new HttpResponse(
                new StatusLine(request.getVersion(), StatusCode.OK),
                headers,
                DEFAULT_BODY.getBytes(StandardCharsets.UTF_8)
        );
    }

    private HttpResponse notFoundResponse(final HttpRequest request) throws IOException {
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

        return new HttpResponse(
                new StatusLine(request.getVersion(), StatusCode.NOT_FOUND),
                headers,
                body.getBytes()
        );
    }
}
