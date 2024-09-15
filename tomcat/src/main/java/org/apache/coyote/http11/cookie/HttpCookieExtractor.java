package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequestHeaders;

public class HttpCookieExtractor {

    private static final String DELIMITER_COOKIE = "; ";

    private static final String DELIMITER_COOKIE_VALUE = "=";

    private static final int INVALID_DELIMITER_INDEX = -1;

    public static HttpCookie extractCookie(HttpRequestHeaders headers) {
        Map<String, String> cookies = new HashMap<>();
        String[] rawCookies = headers.getCookies().split(DELIMITER_COOKIE);
        for (String rawCookie : rawCookies) {
            int delimiterCookieValueIndex = rawCookie.indexOf(DELIMITER_COOKIE_VALUE);
            if (delimiterCookieValueIndex != INVALID_DELIMITER_INDEX) {
                String key = rawCookie.substring(0, delimiterCookieValueIndex);
                String value = rawCookie.substring(delimiterCookieValueIndex + 1);
                cookies.put(key, value);
            }
        }
        return new HttpCookie(cookies);
    }
}
