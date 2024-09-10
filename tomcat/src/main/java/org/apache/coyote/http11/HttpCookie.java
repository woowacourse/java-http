package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie ofSessionId(String sessionId) {
        HttpCookie cookie = new HttpCookie();
        cookie.setSession(sessionId);
        return cookie;
    }

    public HttpCookie(String rawCookies) {
        this.cookies = parse(rawCookies);
    }

    private Map<String, String> parse(String rawCookies) {
        if (rawCookies == null) {
            return Collections.emptyMap();
        }

        return Arrays.stream(rawCookies.split("; "))
                .map(cookie -> cookie.split("="))
                .filter(data -> data.length == 2)
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public String buildMessage() {
        return cookies.keySet().stream()
                .map(key -> key + "=" + cookies.get(key))
                .collect(Collectors.joining("; "));
    }

    public boolean hasSession() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getSession() {
        return cookies.get(JSESSIONID);
    }

    public void setSession(String value) {
        cookies.put(JSESSIONID, value);
    }
}
