package org.apache.coyote.http11.url;

public class HandlerMapping {

    public static Url from(String uri) {
        if (isHomeUrl(uri)) {
            return new HomePage(uri);
        }
        if (uri.startsWith("login")) {
            return new Login(uri);
        }
        if (uri.startsWith("register")) {
            return new Register(uri);
        }
        return new Empty(uri);
    }

    private static boolean isHomeUrl(String uri) {
        return uri.startsWith("index") || uri.endsWith("css") || uri.endsWith("csv") || uri.endsWith("js")
                || uri.endsWith("ico");
    }
}
