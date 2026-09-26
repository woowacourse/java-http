package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ResourceResolver {

    private static final String STATIC = "static";
    private static final String HTML_EXTENSION = ".html";

    private ResourceResolver() {
    }

    public static Optional<String> resolve(String path) throws IOException {
        if (path.equals("/")) {
            return Optional.of("Hello world!");
        }
        String resourcePath = STATIC + path;
        if (!resourcePath.contains(".")) {
            resourcePath += HTML_EXTENSION;
        }
        URL resource = ResourceResolver.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(Path.of(resource.getPath()))));
    }

    public static String resolveContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
