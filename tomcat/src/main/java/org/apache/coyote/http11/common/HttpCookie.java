package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final String SESSION_ID = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new ConcurrentHashMap<>());
    }

    public static HttpCookie request(final String message) {
        final Map<String, String> cookies = new HashMap<>();
        final String[] cookieElements = message.split(COOKIES_DELIMITER);
        for (final String cookie : cookieElements) {
            final String[] cookieElement = cookie.split(COOKIE_DELIMITER);
            cookies.put(cookieElement[KEY], cookieElement[VALUE]);
        }
        return new HttpCookie(cookies);
    }

    public static HttpCookie response(final String message) {
        final Map<String, String> cookies = new HashMap<>();
        cookies.put(SESSION_ID, message);
        return new HttpCookie(cookies);
    }

    public void generateSessionId() {
        if (hasSessionId()) {
            throw new IllegalArgumentException(String.format("Cookie 가 중복적으로 저장되었습니다. [%s]", SESSION_ID));
        }
        cookies.put(SESSION_ID, UUID.randomUUID().toString());
    }

    public boolean hasSessionId() {
        return cookies.containsKey(SESSION_ID);
    }

    public String toMessage() {
        final StringBuilder message = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            message.append(entry.getKey())
                .append(COOKIE_DELIMITER)
                .append(entry.getValue())
                .append(COOKIES_DELIMITER);
        }
        excludeLastEmpty(message);
        return new String(message);
    }

    private void excludeLastEmpty(final StringBuilder message) {
        message.deleteCharAt(message.length() - 1);
        message.deleteCharAt(message.length() - 1);
    }

    public String getSessionId() {
        if (!hasSessionId()) {
            throw new IllegalArgumentException("session id 가 저장되지 않았습니다.");
        }
        return cookies.get(SESSION_ID);
    }
}
