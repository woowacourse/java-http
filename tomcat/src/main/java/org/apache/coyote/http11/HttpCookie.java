package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Cookie 헤더 문자열을 파싱해 이름으로 쿠키 값을 조회할 수 있게 함.
 */
public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(cookies);
        }

        for (String cookie : cookieHeader.split(COOKIE_SEPARATOR)) {
            String[] keyAndValue = cookie.trim().split(KEY_VALUE_SEPARATOR, 2);
            if (keyAndValue.length == 2) {
                cookies.put(keyAndValue[0].trim(), keyAndValue[1].trim());
            }
        }

        return new HttpCookie(cookies);
    }

    public Optional<String> getValue(String name) {
        return Optional.ofNullable(cookies.get(name));
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }
}
