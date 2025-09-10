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

public class NotFoundHandler implements Handler {
    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
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
