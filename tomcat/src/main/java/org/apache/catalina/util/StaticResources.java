package org.apache.catalina.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class StaticResources {

    private static final String STATIC_PREFIX = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String EXTENSION_DELIMITER = ".";

    private StaticResources() {
    }

    public static Optional<String> read(final String path) {
        final String resourcePath = STATIC_PREFIX + addExtensionIfAbsent(path);
        try (final InputStream resource =
                     StaticResources.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                return Optional.empty();
            }
            return Optional.of(new String(resource.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static String addExtensionIfAbsent(final String path) {
        if (path.contains(EXTENSION_DELIMITER)) {
            return path;
        }
        return path + HTML_EXTENSION;
    }
}
