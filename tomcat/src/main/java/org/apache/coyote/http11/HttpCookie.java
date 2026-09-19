package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private static final String PARAM_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private final Map<String, String> cookies;

    public HttpCookie(String cookieLine) {
        cookies = parseCookies(cookieLine);
    }

    private Map<String, String> parseCookies(String cookieLine) {
        if (cookieLine == null) {
            return new HashMap<>();
        }

        final String[] keyValues = cookieLine.split(PARAM_DELIMITER);

        final Map<String, String> cookies = new HashMap<>();
        for (String keyValue : keyValues) {
            final String stripedKeyValue = keyValue.strip();
            final int delimiterIndex = stripedKeyValue.indexOf(KEY_VALUE_DELIMITER);

            final String key = stripedKeyValue.substring(0, delimiterIndex);
            final String value = stripedKeyValue.substring(delimiterIndex + 1);

            cookies.put(key, value);
        }
        return cookies;
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
