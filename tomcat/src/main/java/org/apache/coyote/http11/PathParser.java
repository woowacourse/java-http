package org.apache.coyote.http11;

import java.nio.file.Path;
import java.util.Objects;

public class PathParser {

    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String ROOT = "/";
    private static final String EXTENSION_HTML_SUFFIX = ".html";
    private static final String EXTENSION_PREFIX = ".";
    private static final String QUERY_PARAM_PREFIX = "?";
    private static final int BEGIN_INDEX = 0;

    public static Path parsePath(final String requestURI) {
        final String relativePath = getRelativePath(requestURI);
        return getAbsolutePath(relativePath);
    }

    private static String getRelativePath(final String requestURI) {
        if (!requestURI.equals(ROOT) && !requestURI.contains(EXTENSION_PREFIX)) {
            final var index = requestURI.indexOf(QUERY_PARAM_PREFIX);
            final var path = requestURI.substring(BEGIN_INDEX, index);
            return STATIC_RESOURCE_PREFIX + path + EXTENSION_HTML_SUFFIX;
        }

        if (requestURI.contains(EXTENSION_PREFIX)) {
            return STATIC_RESOURCE_PREFIX + requestURI;
        }
        return requestURI;
    }

    private static Path getAbsolutePath(final String relativePath) {
        if (relativePath.equals(ROOT)) {
            return Path.of(ROOT);
        }

        final var resourceURL = ClassLoader.getSystemClassLoader().getResource(relativePath);
        if (Objects.isNull(resourceURL)) {
            // 404 반환
            System.out.println("null입니다.");
        }
        return Path.of(resourceURL.getPath());
    }
}
