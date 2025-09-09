package org.apache.catalina.handler;

import org.apache.coyote.http11.response.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticResourceHandler {

    private StaticResourceHandler() {
    }

    public static HttpResponse serveStaticResource(final String path) throws Exception {
        final var classpathLocation = "static" + path;
        final var contentType = detectContentType(classpathLocation);
        final var body = readStaticFile(classpathLocation);

        return HttpResponse.builder()
                .status(200, "OK")
                .contentType(contentType)
                .body(body)
                .build();
    }

    private static String detectContentType(final String classpathLocation) throws Exception {
        final var resourceUrl = Objects.requireNonNull(
                StaticResourceHandler.class.getClassLoader()
                        .getResource(classpathLocation),
                "Resource not found: " + classpathLocation
        );
        final var path = Paths.get(resourceUrl.toURI());
        final var contentType = Files.probeContentType(path);

        return contentType != null ? contentType : "text/plain;charset=utf-8";
    }

    private static byte[] readStaticFile(final String classpathLocation) throws Exception {
        final var resourceUrl = Objects.requireNonNull(
                StaticResourceHandler.class.getClassLoader()
                        .getResource(classpathLocation),
                "Resource not found: " + classpathLocation
        );
        final var resourceUri = resourceUrl.toURI();

        return Files.readAllBytes(Paths.get(resourceUri));
    }
}
