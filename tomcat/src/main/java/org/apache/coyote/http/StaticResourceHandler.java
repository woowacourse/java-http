package org.apache.coyote.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class StaticResourceHandler {

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

    private static final String DEFAULT_PATH = "static";

    private static final List<String> STATIC_EXTENSIONS = List.of(".html", ".css", ".js", ".svg", ".ico");

    public static boolean isStaticResource(final HttpRequest request) {
        String path = request.getPath();
        return STATIC_EXTENSIONS.stream().anyMatch(path::endsWith);
    }

    public static String handle(final String requestPath) {
        Path path = resourcePath(requestPath);
        String resourceAsString;
        try {
            resourceAsString = Files.readString(path);
        } catch (IOException e) {
            throw new NoResourceFoundException(path.toString());
        }
        return resourceAsString;
    }

    private static Path resourcePath(final String path) {
        final var url = CLASS_LOADER.getResource(DEFAULT_PATH + path);
        if (url == null) {
            throw new NoResourceFoundException(path);
        }
        return Path.of(url.getPath());
    }
}
