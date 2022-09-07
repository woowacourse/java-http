package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String COOKIES_DELIMITER = "; ";
    private static final String KEY_OF_SESSION_ID = "JSESSIONID";

    private final Map<String, String> cookies;

    public static Cookies empty() {
        return new Cookies();
    }

    public static Cookies from(final String cookies) {
        if (cookies == null || cookies.isBlank()) {
            return Cookies.empty();
        }

        Map<String, String> map = new HashMap<>();

        for (String cookie : cookies.split(COOKIES_DELIMITER)) {
            final String key = cookie.split(KEY_VALUE_DELIMITER)[0];
            final String value = cookie.split(KEY_VALUE_DELIMITER)[1];
            map.put(key, value);
        }

        return new Cookies(map);
    }

    private Cookies() {
        this(new HashMap<>());
    }

    private Cookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean hasSessionId() {
        return cookies.containsKey(KEY_OF_SESSION_ID);
    }

    public String getSessionId() {
        if (!hasSessionId()) {
            throw new IllegalStateException("쿠키에 세션 ID가 없습니다.");
        }
        return cookies.get(KEY_OF_SESSION_ID);
    }

    public void addCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public boolean hasCookies() {
        return !cookies.isEmpty();
    }

    public String parseToHeaderValue() {
        if (hasCookies()) {
            return cookies.keySet().stream()
                    .map(name -> name + KEY_VALUE_DELIMITER + cookies.get(name))
                    .collect(Collectors.joining(COOKIES_DELIMITER));
        }
        return "";
    }
}
