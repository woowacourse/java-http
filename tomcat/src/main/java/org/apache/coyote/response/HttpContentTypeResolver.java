package org.apache.coyote.response;

import java.util.Locale;

public class HttpContentTypeResolver {

    public static String resolve(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "application/octet-stream";
    }
}
