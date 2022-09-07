package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {
    public static Url from(HttpRequest request) {
        String path = request.getPath();

        if (isHomeUrl(request.getPath())) {
            return new HomePage(request);
        }
        if (path.startsWith("/login")) {
            return new Login(request);
        }
        if (path.startsWith("/register")) {
            return new Register(request);
        }
        return new Empty(request);
    }

    private static boolean isHomeUrl(String uri) {
        return uri.startsWith("/index") || uri.endsWith("css") || uri.endsWith("csv") || uri.endsWith("js")
                || uri.endsWith("ico");
    }
}
