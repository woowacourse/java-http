package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    public static final String SESSION_NAME = "JSESSIONID";
    public static final String COOKIES_DELIMITER = "; ";
    public static final String VALUE_DELIMITER = "=";


    private final Map<String, String> cookies;

    public HttpCookies() {
        this.cookies = new HashMap<>();
    }

    public HttpCookies(String rawCookies) {
        this.cookies = buildCookie(rawCookies);
    }

    private Map<String, String> buildCookie(String rawCookies) {
        return Arrays.stream(rawCookies.split(COOKIES_DELIMITER))
                .map(rawCookie -> rawCookie.split(VALUE_DELIMITER))
                .filter(cookiePair -> cookiePair.length == 2)
                .collect(Collectors.toMap(
                        cookiePair -> cookiePair[0],
                        cookiePair -> cookiePair[1]
                ));
    }

    public HttpCookies addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public String getCookieValue(String name) {
        return cookies.getOrDefault(name, "");
    }

    public String buildOutput() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIES_DELIMITER));
    }

    public String getSessionId() {
        return getCookieValue(SESSION_NAME);
    }

    public void addSession(String sessionId) {
        cookies.put(SESSION_NAME, sessionId);
    }
}
