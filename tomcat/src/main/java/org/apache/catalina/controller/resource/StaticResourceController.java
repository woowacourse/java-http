package org.apache.catalina.controller.resource;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public final class StaticResourceController extends AbstractController {

    private static final Map<String, String> MIME_TYPE_TO_EXTENSION = Map.of(
            "text/html", ".html"
    );

    private static final List<String> SUPPORTED_MIME_TYPES_BY_PREFERENCE = List.of(
            "text/html"
    );

    @Override
    protected void doGet(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        final String path = request.getPath();
        findResource(path)
                .ifPresentOrElse(
                        resource -> {
                            response.setStatus(200);
                            response.setBody(resource.body(), resource.contentType());
                        },
                        () -> handleUnmatchedRequest(request, response)
                );
    }

    private void handleUnmatchedRequest(final Http11Request request, final Http11Response response) {
        final String path = request.getPath();
        final var headers = request.getHeaders();
        final String acceptHeader = headers.getHeader("Accept");
        if (acceptHeader == null || acceptHeader.isBlank()) {
            serveDefaultAsHtml(path, response);
            return;
        }
        for (final String mimeType : SUPPORTED_MIME_TYPES_BY_PREFERENCE) {
            if (acceptHeader.contains(mimeType)) {
                final String extension = MIME_TYPE_TO_EXTENSION.get(mimeType);
                final var resourceOptional = findResource(path + extension);
                if (resourceOptional.isPresent()) {
                    response.setStatus(200);
                    response.setBody(resourceOptional.get().body(), resourceOptional.get().contentType());
                    return;
                }
            }
        }
        serveNotFoundPage(response);
    }

    private void serveDefaultAsHtml(
            final String path,
            final Http11Response response
    ) {
        findResource(path + ".html")
                .ifPresentOrElse(
                        resource -> {
                            response.setStatus(200);
                            response.setBody(resource.body(), resource.contentType());
                        },
                        () -> serveNotFoundPage(response)
                );
    }

    private Optional<Resource> findResource(final String path) {
        return readStaticResource(path)
                .map(body -> new Resource(body, getContentType(path)));
    }

    private void serveNotFoundPage(final Http11Response response) {
        readStaticResource("/404.html")
                .ifPresentOrElse(
                        body -> {
                            response.setStatus(404);
                            response.setBody(body, "text/html;charset=utf-8");
                        },
                        () -> {
                            response.setStatus(404);
                            response.setBody("404 Not Found", "text/html;charset=utf-8");
                        }
                );
    }

    private Optional<byte[]> readStaticResource(final String path) {
        final String resourcePath = "static" + path;
        try (final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
