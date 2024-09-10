package org.apache.coyote.http11.httprequest;

import java.util.Arrays;

public class HttpCookieConvertor {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_TOKEN_DELIMITER = "=";

    public static HttpCookie convertHttpCookie(String rowCookie) {
        HttpCookie httpCookie = new HttpCookie();
        String[] cookieTokens = rowCookie.split(COOKIE_DELIMITER);
        Arrays.stream(cookieTokens)
                .filter(HttpCookieConvertor::filterCookie)
                .map(cookieToken -> cookieToken.split(COOKIE_TOKEN_DELIMITER))
                .forEach(cookie -> httpCookie.addCookie(cookie[0], cookie[1]));

        return httpCookie;
    }

    private static boolean filterCookie(String cookie) {
        return cookie.split(COOKIE_TOKEN_DELIMITER).length >= 2;
    }
}
