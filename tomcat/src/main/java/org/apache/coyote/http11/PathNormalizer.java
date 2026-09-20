package org.apache.coyote.http11;

import java.util.Map;

public final class PathNormalizer {

    private static final Map<String, String> PATH_ALIASES = Map.of(
        "/index.html", "/index",
        "/login.html", "/login",
        "/register.html", "/register"
    );

    public static String normalize(final String path) {
        return PATH_ALIASES.getOrDefault(path, path);
    }
}
