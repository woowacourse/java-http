package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.CookieFormatException;

public class HttpCookie {

    private static final String COOKIE_CONNECTOR = " ";
    private static final String COOKIE_SEPARATOR = "=";
    private static final String JSESSIONID = "JSESSIONID";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public HttpCookie(final String rawCookie) {
        this.values = new HashMap<>();

        if (rawCookie == null) {
            throw new CookieFormatException();
        }
        String[] cookies = rawCookie.split(COOKIE_CONNECTOR);
        addCookies(cookies);
    }

    private void addCookies(String[] cookies) {
        for (String cookie : cookies) {
            validateCookieFormat(cookie);
            String[] value = cookie.split(COOKIE_SEPARATOR);
            this.values.put(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }

    private void validateCookieFormat(final String cookie) {
        if (!cookie.contains(COOKIE_SEPARATOR) || cookie.isBlank()) {
            throw new CookieFormatException();
        }
        if (cookie.split(COOKIE_SEPARATOR).length != 2) {
            throw new CookieFormatException();
        }
    }

    public static HttpCookie fromJSessionId(final String sessionId) {
        return new HttpCookie(Map.of(JSESSIONID, sessionId));
    }

    public String getSession() {
        return JSESSIONID + "=" + values.get(JSESSIONID);
    }

    public String getSessionId() {
        return values.get(JSESSIONID);
    }
}
