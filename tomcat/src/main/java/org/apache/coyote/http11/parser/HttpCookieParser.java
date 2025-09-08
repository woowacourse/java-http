package org.apache.coyote.http11.parser;

import static org.apache.coyote.http11.HttpConstants.EMPTY;
import static org.apache.coyote.http11.HttpConstants.EQUAL;
import static org.apache.coyote.http11.HttpConstants.SEMICOLON;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.HttpCookie;

public class HttpCookieParser {

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
