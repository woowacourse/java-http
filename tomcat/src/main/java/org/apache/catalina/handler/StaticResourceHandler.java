package org.apache.catalina.handler;

import org.apache.coyote.http11.response.HttpResponse;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticResourceHandler {

    private StaticResourceHandler() {
    }

    public static HttpResponse serveStaticResource(final String path) throws Exception {
        final var classpathLocation = "static" + path;
        final var resourceUrl = StaticResourceHandler.class.getClassLoader()
                .getResource(classpathLocation);
        if (resourceUrl == null) {
            return HttpResponse.builder()
                    .status(404, "Not Found")
                    .contentType("text/plain;charset=utf-8")
                    .body(("Resource not found: " + path).getBytes())
                    .build();
        }

        final var contentType = detectContentType(resourceUrl);
        final var body = Files.readAllBytes(Paths.get(resourceUrl.toURI()));

        return HttpResponse.builder()
                .status(200, "OK")
                .contentType(contentType)
                .body(body)
                .build();
    }

    private static String detectContentType(final URL resourceUrl) throws Exception {
        final var path = Paths.get(Objects.requireNonNull(resourceUrl)
                .toURI());
        final var contentType = Files.probeContentType(path);
        
        return contentType != null ? contentType : "text/plain;charset=utf-8";
    }
}
