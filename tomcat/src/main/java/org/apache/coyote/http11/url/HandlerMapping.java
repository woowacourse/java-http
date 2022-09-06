package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpMethod;

public class HandlerMapping {

    public static Url from(String uri, Http11Request request) {
        if (isHomeUrl(uri)) {
            return new HomePage(uri, request);
        }
        if (uri.startsWith("/login")) {
            return new Login(uri, request);
        }
        if (uri.startsWith("/register")) {
            return new Register(uri, request);
        }
        return new Empty(uri, request);
    }

    private static boolean isHomeUrl(String uri) {
        return uri.startsWith("/index") || uri.endsWith("css") || uri.endsWith("csv") || uri.endsWith("js")
                || uri.endsWith("ico");
    }
}
