package org.apache.coyote.http11.handler;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

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
    protected HttpResponse handleGet(String request) {
        Resource responseBody = getResource();
        MimeType mimeType = MimeType.fromResource(responseBody);
        return new HttpResponse(HttpStatus.OK, responseBody.content(), mimeType, Map.of());
    }

    private Resource getResource() {
        try {
            return Resource.fromPath("static" + resourcePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
