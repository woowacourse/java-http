package org.apache.coyote.http11.parser;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.HttpCookie;

public class HttpCookieParser {

    private static final String SEMICOLON = ";";
    private static final String EQUAL = "=";
    private static final String EMPTY = "";

    private HttpCookieParser() {
    }

    public static HttpCookie parse(final String cookieString) {
        final Map<String, String> cookies = new LinkedHashMap<>();
        final String[] pairs = cookieString.split(SEMICOLON);

        for (final String pair : pairs) {
            final String[] keyValue = pair.trim().split(EQUAL, 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                cookies.put(keyValue[0], EMPTY);
            }
        }

        return new HttpCookie(cookies);
    }
}
