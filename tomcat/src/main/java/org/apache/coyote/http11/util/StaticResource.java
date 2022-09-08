package org.apache.coyote.http11.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.http11.common.HttpRequest;

public class StaticResource {

    private static final String STATIC_PREFIX = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String EXTENSION_DOT = ".";

    public static final String INDEX_PATH = "/index";
    public static final String UNAUTHORIZED_PATH = "/401";
    public static final String NOT_FOUND_PATH = "/404";
    public static final String INTERNAL_SERVER_ERROR_PATH = "/500";

    private StaticResource() {
    }

    public static Path ofRequest(final HttpRequest request) {
        final String resourceName = request.getPath();
        return findPath(toResourceName(resourceName));
    }

    public static Path notFound() {
        return findPath(toResourceName(NOT_FOUND_PATH));
    }

    public static Path internalServerError() {
        return findPath(toResourceName(INTERNAL_SERVER_ERROR_PATH));
    }

    public static Path unauthorized() {
        return findPath(toResourceName(UNAUTHORIZED_PATH));
    }

    private static Path findPath(final String resourceName) {
        try {
            final URL resource = findResource(resourceName);
            if (resource == null) {
                throw new ResourceNotFoundException();
            }

            return Path.of(resource.toURI());
        } catch (final URISyntaxException e) {
            throw new ResourceNotFoundException();
        }
    }

    private static URL findResource(final String resourceName) {
        return StaticResource.class
                .getClassLoader()
                .getResource(resourceName);
    }

    private static String toResourceName(final String rawResourceName) {
        if (!rawResourceName.contains(EXTENSION_DOT)) {
            return String.join("", STATIC_PREFIX, rawResourceName, HTML_EXTENSION);
        }

        return String.join("", STATIC_PREFIX, rawResourceName);
    }
}
