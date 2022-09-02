package org.apache.coyote.http11.url;

public class HandlerMapping {

    public static Url from(String uri) {
        if (uri.startsWith("index") || uri.startsWith("css") || uri.startsWith("csv")) {
            return new HomePage(uri);
        }
        if (uri.startsWith("login")) {
            return new Login(uri);
        }
        return new Empty(uri);
    }
}
