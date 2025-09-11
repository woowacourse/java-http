package org.apache.coyote.http11.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class ResourceLoader {

    private static final String DEFAULT_RESOURCE = "static/404.html";
    private static final String STATIC_DIR = "static";

    private ResourceLoader() {
    }

    public static byte[] getResourceBytes(String uri) throws URISyntaxException, IOException {
        Path path = getResourcePath(uri);
        return Files.readAllBytes(path);
    }

    public static String getResourceContentType(String uri) throws IOException, URISyntaxException, IOException {
        Path path = getResourcePath(uri);
        String contentType = Files.probeContentType(path);

        if (contentType != null && contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }

        if (contentType != null) {
            return contentType;
        }

        return "application/octet-stream";
    }


    private static Path getResourcePath(String uri) throws URISyntaxException {
        String resourcePath = STATIC_DIR + uri;

        URL resourceUrl = ResourceLoader.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            resourceUrl = Objects.requireNonNull(
                    ResourceLoader.class.getClassLoader().getResource(DEFAULT_RESOURCE)
            );
        }

        return Paths.get(resourceUrl.toURI());
    }
}

