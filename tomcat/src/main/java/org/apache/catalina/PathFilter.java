package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import java.util.Map;

public class PathFilter {

    private static final Map<String, String> PATH_TO_PAGE_MAPPING = Map.of(
            "/login", "/login.html",
            "/", "/home.html",
            "/register", "/register.html");

    private PathFilter() {
    }

    public static void doFilter(HttpRequest httpRequest) {
        String path = httpRequest.getPath();

        if (PATH_TO_PAGE_MAPPING.containsKey(path)) {
            httpRequest.setPath(PATH_TO_PAGE_MAPPING.get(path));
        }
    }
}
