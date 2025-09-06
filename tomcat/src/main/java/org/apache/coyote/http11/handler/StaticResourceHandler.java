package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class StaticResourceHandler extends HttpRequestHandler {

    protected final String resourcePath;

    public StaticResourceHandler(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    String getSupportedUrl() {
        return resourcePath;
    }

    @Override
    protected String handleGet(String request) {
        Resource responseBody = getResource();
        MimeType mimeType = MimeType.fromResource(responseBody);
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: " + mimeType.getMimeType(),
                "Content-Length: " + responseBody.content().getBytes(StandardCharsets.UTF_8).length,
                "",
                responseBody.content()
        );
    }

    private Resource getResource() {
        try {
            return Resource.fromPath("static" + resourcePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
