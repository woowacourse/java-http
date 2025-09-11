package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static final String DEFAULT_MIMETYPE = "application/octet-stream";

    private final String base;
    private final String defaultDocument;

    public StaticResourceController(String base, String defaultDocument) {
        this.base = base;
        this.defaultDocument = defaultDocument;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resourcePath = request.path().replaceFirst("^/static/?", "");
        String normalizedResourcePath = normalizePath(resourcePath);
        String fullPath = base + "/" + normalizedResourcePath;

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(fullPath)) {
            if (resourceStream == null) {
                // FIXME: 예외처리해서 404 처리 하는게 좋것 같아
                response.status(HttpStatus.NOT_FOUND)
                        .contentType(ContentType.PLAIN.value())
                        .write("404 Not Found");
                return;
            }

            byte[] bytes = resourceStream.readAllBytes();
            String contentType = resolveContentType(normalizedResourcePath);

            response.status(HttpStatus.OK)
                    .contentType(contentType)
                    .write(bytes);

        } catch (IOException e) {
            // FIXME: 예외처리해서 404 처리 하는게 좋것 같아
            response.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(ContentType.PLAIN.value())
                    .write("500 Internal Server Error");
        }
    }

    private String normalizePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.isEmpty()) {
            path = defaultDocument;
        }
        if (!path.contains(".")) {
            path = path + ".html";
        }
        return path;
    }

    private String resolveContentType(String resourcePath) {
        try {
            Path path = Path.of(resourcePath);
            String mimeType = Files.probeContentType(path);
            return (mimeType != null ? mimeType : DEFAULT_MIMETYPE);
        } catch (IOException e) {
            return DEFAULT_MIMETYPE;
        }
    }
}
