package org.apache.coyote.http11.resolver;

public class PathResolver {

    private PathResolver() {
    }

    public static String resolve(final String route) {
        if (route == null || route.isBlank() || "/".equals(route)) {
            return "index.html";
        }

        String resolved = route;
        if (route.startsWith("/login") && !route.endsWith(".html")) {
            resolved = route + ".html";
        }
        return resolved.substring(route.indexOf("/") + 1);
    }
}
