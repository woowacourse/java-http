package org.apache.catalina.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class StaticResourceController extends MethodDispatchingController {
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String NOT_FOUND_RESPONSE_BODY = "404 Not Found";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        serve(request.path(), response);
    }

    public void serve(final String requestPath, final HttpResponse response) throws IOException {
        final Optional<byte[]> resourceBody = findResourceBody(requestPath);
        if (resourceBody.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody(HTML_CONTENT_TYPE, readNotFoundResponseBody());
            return;
        }
        response.setBody(resolveContentType(requestPath), resourceBody.get());
    }

    private Optional<byte[]> findResourceBody(final String requestPath) throws IOException {
        final String resourcePath = "static" + requestPath;
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        }
    }

    private byte[] readNotFoundResponseBody() throws IOException {
        return findResourceBody(NOT_FOUND_PATH)
                .orElseGet(() -> NOT_FOUND_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8));
    }

    private String resolveContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }
        return HTML_CONTENT_TYPE;
    }
}
