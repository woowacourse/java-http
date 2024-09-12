package org.apache.coyote.http;

import org.apache.coyote.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String SESSION = "JSESSIONID";

    private final Map<String, String> cookies;

    public static HttpCookie of(String bulkCookie) {
        if (bulkCookie == null || bulkCookie.isEmpty()) {
            throw new IllegalArgumentException("Invalid cookie " + bulkCookie);
        }
        return new HttpCookie(StringUtils.separateKeyValue(bulkCookie, COOKIE_KEY_VALUE_SEPARATOR, COOKIE_SEPARATOR));
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean hasCookieName(String cookieName) {
        return cookies.containsKey(cookieName);
    }

    public String getCookieValue(String cookieName) {
        if (!cookies.containsKey(cookieName)) {
            throw new IllegalArgumentException("Invalid cookie " + cookieName);
        }
        return cookies.get(cookieName);
    }

    public void setSessionId(String sessionId) {
        cookies.put(SESSION, sessionId);
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    public String toCookieResponse() {
        return cookies.entrySet().stream()
                .map(c -> c.getKey() + COOKIE_KEY_VALUE_SEPARATOR + c.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
