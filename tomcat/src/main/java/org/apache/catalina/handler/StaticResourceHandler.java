package org.apache.catalina.handler;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticResourceHandler {

    private StaticResourceHandler() {
    }

    public static HttpResponse serveStaticResource(final HttpRequest request) throws URISyntaxException, IOException {
        final var rootPath = getStaticRootPath();
        final var requestedPath = resolveRequestedPath(rootPath, request.getPath());

        if (!Files.exists(requestedPath) || Files.isDirectory(requestedPath)) {
            return notFoundResponse(request);
        }

        return okResponse(request, requestedPath);
    }

    private static Path getStaticRootPath() throws URISyntaxException {
        final var rootUrl = Objects.requireNonNull(
                StaticResourceHandler.class.getClassLoader()
                        .getResource("static"),
                "Static root not found"
        );

        return Paths.get(rootUrl.toURI())
                .normalize();
    }

    private static Path resolveRequestedPath(final Path rootPath, final String path) throws IOException {
        final var relativePath = path.startsWith("/") ? path.substring(1) : path;

        return rootPath.resolve(relativePath)
                .normalize();
    }

    private static HttpResponse notFoundResponse(final HttpRequest request) {
        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(302, "Found")
                .header("Location", "/404.html")
                .contentType("text/html;charset=utf-8")
                .build();
    }

    private static HttpResponse okResponse(final HttpRequest request, final Path requestedPath) throws IOException {
        final var contentType = Files.probeContentType(requestedPath);
        final var body = Files.readAllBytes(requestedPath);

        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(200, "OK")
                .contentType(contentType != null ? contentType : "text/plain;charset=utf-8")
                .body(body)
                .build();
    }
}
