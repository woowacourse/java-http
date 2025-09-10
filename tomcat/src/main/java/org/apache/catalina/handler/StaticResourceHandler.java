package org.apache.catalina.handler;

import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticResourceHandler {

    private StaticResourceHandler() {
    }

    public static HttpResponse serveStaticResource(final String path) {
        try {
            final var rootPath = getStaticRootPath();
            final var requestedPath = resolveRequestedPath(rootPath, path);

            if (!isUnderRoot(rootPath, requestedPath)) {
                return forbiddenResponse();
            }

            if (!Files.exists(requestedPath) || Files.isDirectory(requestedPath)) {
                return notFoundResponse(path);
            }

            return okResponse(requestedPath);
        } catch (URISyntaxException | IOException e) {
            return notFoundResponse(path);
        }
    }

    private static Path getStaticRootPath() throws URISyntaxException, IOException {
        final var rootUrl = Objects.requireNonNull(
                StaticResourceHandler.class.getClassLoader()
                        .getResource("static"),
                "Static root not found"
        );
        
        return Paths.get(rootUrl.toURI())
                .toRealPath();
    }

    private static Path resolveRequestedPath(final Path rootPath, final String path) throws IOException {
        final var relativePath = path.startsWith("/") ? path.substring(1) : path;

        return rootPath.resolve(relativePath)
                .toRealPath();
    }

    private static boolean isUnderRoot(final Path rootPath, final Path requestedPath) {
        return requestedPath.startsWith(rootPath);
    }

    private static HttpResponse forbiddenResponse() {
        return HttpResponse.builder()
                .status(403, "Forbidden")
                .contentType("text/plain;charset=utf-8")
                .body("Forbidden".getBytes())
                .build();
    }

    private static HttpResponse notFoundResponse(final String path) {
        return HttpResponse.builder()
                .status(404, "Not Found")
                .contentType("text/plain;charset=utf-8")
                .body(("Resource not found: " + path).getBytes())
                .build();
    }

    private static HttpResponse okResponse(final Path requestedPath) throws IOException {
        final var contentType = Files.probeContentType(requestedPath);
        final var body = Files.readAllBytes(requestedPath);

        return HttpResponse.builder()
                .status(200, "OK")
                .contentType(contentType != null ? contentType : "text/plain;charset=utf-8")
                .body(body)
                .build();
    }
}
