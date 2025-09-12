package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.controller.exception.ResourceNotFoundException;

public final class StaticResourceResolver {

    private static final String STATIC_DIRECTORY = "static/";

    private StaticResourceResolver() {
    }

    public static String read(final String path) throws IOException {
        final URL resourceUrl = findResource(path);
        if (resourceUrl == null) {
            throw new ResourceNotFoundException(path);
        }

        try (final InputStream inputStream = resourceUrl.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static URL findResource(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(STATIC_DIRECTORY + path);
        if (resource == null) {
            resource = classLoader.getResource(STATIC_DIRECTORY + path + ".html");
        }
        return resource;
    }
}
