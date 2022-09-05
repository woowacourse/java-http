package org.apache.coyote.http11.util;

import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.exception.NotFoundResourceException;

public class PathParser {

    private static final String STATIC_RESOURCE_PREFIX = "static";

    public static Path parsePath(final String requestURI) {
        final String relativePath = STATIC_RESOURCE_PREFIX + requestURI;
        return getAbsolutePath(relativePath);
    }

    private static Path getAbsolutePath(final String relativePath) {
        final var resourceURL = ClassLoader.getSystemClassLoader().getResource(relativePath);
        if (Objects.isNull(resourceURL)) {
            throw new NotFoundResourceException();
        }
        return Path.of(resourceURL.getPath());
    }
}
