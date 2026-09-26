package org.apache.catalina;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class StaticResource {

    private static final String STATIC_ROOT = "static/";

    public Optional<byte[]> read(String requestPath) throws IOException, URISyntaxException {
        URL resource = find(requestPath);
        if (resource == null) {
            return Optional.empty();
        }
        return Optional.of(Files.readAllBytes(Path.of(resource.toURI())));
    }

    private URL find(String requestPath) {
        String resourcePath = normalize(requestPath);
        URL resource = getClass().getClassLoader().getResource(STATIC_ROOT + resourcePath);
        if (resource != null || resourcePath.endsWith(".html")) {
            return resource;
        }
        return getClass().getClassLoader().getResource(STATIC_ROOT + resourcePath + ".html");
    }

    private String normalize(String requestPath) {
        String resourcePath = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
        if (resourcePath.contains("..")) {
            throw new IllegalArgumentException("Invalid resource path: " + requestPath);
        }
        return resourcePath;
    }
}
