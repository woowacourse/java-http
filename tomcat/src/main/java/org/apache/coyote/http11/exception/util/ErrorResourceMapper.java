package org.apache.coyote.http11.exception.util;

import java.util.HashMap;
import java.util.Map;

public class ErrorResourceMapper {

    private static final Map<Integer, String> RESOURCES = new HashMap<>();

    static {
        RESOURCES.put(400, "/static/400.html");
        RESOURCES.put(401, "/static/401.html");
        RESOURCES.put(404, "/static/404.html");
        RESOURCES.put(500, "/static/500.html");
    }

    public static String getResource(final int code) {
        return RESOURCES.getOrDefault(code, "/static/500.html");
    }
}
