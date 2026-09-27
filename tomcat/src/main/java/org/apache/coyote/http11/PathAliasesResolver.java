package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.error.HttpException;

public final class PathAliasesResolver {

    private static final Map<String, String> PATH_ALIASES = Map.of(
        "/index.html", "/index",
        "/login.html", "/login",
        "/register.html", "/register"
    );

    public static String normalize(final String path) {
        if (!path.startsWith("/")) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "path는 '/'로 시작되어야 합니다: " + path);
        }
        return PATH_ALIASES.getOrDefault(path, path);
    }

    private PathAliasesResolver() {}
}
