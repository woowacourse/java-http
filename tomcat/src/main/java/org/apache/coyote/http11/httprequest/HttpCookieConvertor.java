package org.apache.coyote.http11.httprequest;

import java.util.Arrays;

public class HttpCookieConvertor {

    public static HttpCookie convertHttpCookie(String rowCookie) {
        HttpCookie httpCookie = new HttpCookie();
        String[] cookieTokens = rowCookie.split("; ");
        Arrays.stream(cookieTokens)
                .filter(HttpCookieConvertor::filterCookie)
                .map(cookieToken -> cookieToken.split("="))
                .forEach(cookie -> httpCookie.addCookie(cookie[0], cookie[1]));

        return httpCookie;
    }

    private static boolean filterCookie(String cookie) {
        return cookie.split("=").length >= 2;
    }
}
