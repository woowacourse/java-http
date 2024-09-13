package org.apache.coyote.http11.httprequest;

import java.util.Arrays;

public class HttpCookieConvertor {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_TOKEN_DELIMITER = "=";
    private static final int COOKIE_TOKEN_MIN_LENGTH = 2;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    public static HttpCookie convertHttpCookie(String rowCookie) {
        HttpCookie httpCookie = new HttpCookie();
        String[] cookieTokens = rowCookie.split(COOKIE_DELIMITER);
        Arrays.stream(cookieTokens)
                .filter(HttpCookieConvertor::filterCookie)
                .map(cookieToken -> cookieToken.split(COOKIE_TOKEN_DELIMITER))
                .forEach(cookie -> httpCookie.addCookie(cookie[COOKIE_KEY_INDEX], cookie[COOKIE_VALUE_INDEX]));

        return httpCookie;
    }

    private static boolean filterCookie(String cookie) {
        return cookie.split(COOKIE_TOKEN_DELIMITER).length >= COOKIE_TOKEN_MIN_LENGTH;
    }
}
