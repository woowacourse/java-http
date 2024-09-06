package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {

    private static final String SESSION = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie of(String bulkCookie) {
        validate(bulkCookie);
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

    private void validate(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            throw new IllegalArgumentException("Invalid cookie " + cookie);
        }
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
        return cookies.entrySet().stream().map(c -> c.getKey().concat("=").concat(c.getValue())).collect(Collectors.joining("; "));
    }
}
