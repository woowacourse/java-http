package org.apache.coyote.http11.resolver;

import java.util.Set;

public final class PathResolver {

    private static final Set<String> HTML_PATHS = Set.of("/login", "/register");

    private PathResolver() {
    }

    public static String resolve(final String route) {
        if (route == null || route.isBlank() || "/".equals(route)) {
            return "index.html";
        }

        String resolved = route;
        if (HTML_PATHS.stream().anyMatch(route::startsWith) && !route.endsWith(".html")) {
            resolved = route + ".html";
        }

        return resolved.substring(1);
    }
}
