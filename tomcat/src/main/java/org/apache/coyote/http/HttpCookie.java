package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {

    private static final String SESSION = "JSESSIONID";

    private final Map<String, String> cookies;

    public static HttpCookie of(String bulkCookie) {
        if (bulkCookie == null || bulkCookie.isEmpty()) {
            throw new IllegalArgumentException("Invalid cookie " + bulkCookie);
        }
        Map<String, String> cookie = Stream.of(bulkCookie.split(";"))
                .map(String::trim)
                .map(c -> c.split("="))
                .collect(Collectors.toMap(c -> c[0], c -> c[1]));
        return new HttpCookie(cookie);
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
                .map(c -> c.getKey() + "=" + c.getValue())
                .collect(Collectors.joining("; "));
    }
}
