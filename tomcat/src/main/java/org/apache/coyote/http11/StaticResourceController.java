package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

final class StaticResourceController extends AbstractController {
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return resourceResponseFor(request.path(), request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        return doGet(request);
    }

    private HttpResponse resourceResponseFor(final String requestPath, final HttpRequest request) throws IOException {
        if (requestPath.equals("/")) {
            return ResponseFactory.resourceResponse(
                    "200 OK",
                    "Hello world!".getBytes(StandardCharsets.UTF_8),
                    HTML_CONTENT_TYPE,
                    request
            );
        }

        final var staticPath = staticPathFor(requestPath);
        final var resourcePath = "static" + staticPath;

        try (final var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                final var body = "404 Not Found".getBytes(StandardCharsets.UTF_8);

                return ResponseFactory.resourceResponse("404 Not Found", body, HTML_CONTENT_TYPE, request);
            }

            return ResponseFactory.resourceResponse(
                    "200 OK",
                    resourceStream.readAllBytes(),
                    contentTypeFor(staticPath),
                    request
            );
        }
    }

    private String staticPathFor(final String requestPath) {
        final var lastSlashIndex = requestPath.lastIndexOf('/');
        final var lastDotIndex = requestPath.lastIndexOf('.');

        if (lastDotIndex > lastSlashIndex) {
            return requestPath;
        }

        return requestPath + ".html";
    }

    private String contentTypeFor(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }

        return HTML_CONTENT_TYPE;
    }
}
