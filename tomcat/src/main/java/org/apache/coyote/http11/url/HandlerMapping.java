package org.apache.coyote.http11.url;

public class HandlerMapping {

    public static Url from(String uri, String httpMethod) {
        if (isHomeUrl(uri)) {
            return new HomePage(uri, httpMethod);
        }
        if (uri.startsWith("/login")) {
            return new Login(uri, httpMethod);
        }
        if (uri.startsWith("/register")) {
            return new Register(uri, httpMethod);
        }
        return new Empty(uri);
    }

    private static boolean isHomeUrl(String uri) {
        return uri.startsWith("/index") || uri.endsWith("css") || uri.endsWith("csv") || uri.endsWith("js")
                || uri.endsWith("ico");
    }
}
