package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {
    private static final String JSESSIONID = "JSESSIONID";
    private static final String HEADER_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";
    private static final int COOKIE_LIMIT = 2;
    private static final int COOKIE_KEY_POSITION = 0;
    private static final int COOKIE_VALUE_POSITION = 1;

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (Objects.nonNull(cookieHeader) && !cookieHeader.isEmpty()) {
            parseCookies(cookieHeader);
        }
    }

    private boolean isNotEmpty(String[] keyValue) {
        return Objects.nonNull(keyValue[COOKIE_KEY_POSITION]) && Objects.nonNull(keyValue[COOKIE_VALUE_POSITION])
                && !keyValue[COOKIE_KEY_POSITION].isEmpty() && !keyValue[COOKIE_VALUE_POSITION].isEmpty();
    }

    private void parseCookies(String cookieHeader) {
        String[] cookiePairs = cookieHeader.split(HEADER_DELIMITER);
        for (String cookiePair : cookiePairs) {
            String[] keyValue = cookiePair.split(COOKIE_DELIMITER, COOKIE_LIMIT);
            if (keyValue.length == COOKIE_LIMIT && isNotEmpty(keyValue)) {
                String key = URLDecoder.decode(keyValue[0].trim(), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1].trim(), StandardCharsets.UTF_8);
                cookies.put(key, value);
            }
        }
    }

    public static String ofJSessionId(String value) {
        return JSESSIONID + COOKIE_DELIMITER + value;
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJsessionid() {
        return cookies.get(JSESSIONID);
    }
}
