package org.apache.coyote.http11.common;

import java.util.UUID;

public class HttpCookie {

    public static final String COOKIES_DELIMITER = ": ";
    public static final String COOKIE_DELIMITER = "=";
    public static final int KEY = 0;
    public static final int VALUE = 1;

    private final String jSessionId;

    private HttpCookie(final String jSessionId) {
        this.jSessionId = jSessionId;
    }

    public static HttpCookie init() {
        final UUID uuid = UUID.randomUUID();

        return new HttpCookie(uuid.toString());
    }

    public static HttpCookie init(final String message) {
        final String[] cookieElements = message.split(COOKIES_DELIMITER);
        for (final String cookie : cookieElements) {
            final String[] cookieElement = cookie.split(COOKIE_DELIMITER);
            if (cookieElement[KEY].equals("JSESSIONID")) {
                return new HttpCookie(cookieElement[VALUE]);
            }
        }
        throw new IllegalArgumentException("session ID 가 존재하지 않습니다.");
    }

    public String getJSessionId() {
        return String.format("JSESSIONID=%s", jSessionId);
    }
}
