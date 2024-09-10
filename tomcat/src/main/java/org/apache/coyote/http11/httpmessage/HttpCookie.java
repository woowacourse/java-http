package org.apache.coyote.http11.httpmessage;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String COOKIE_DELIMITER = "; ";
    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie parseFrom(String cookiesLine) {
        HashMap<String, String> cookies = new HashMap<>();
        for (String cookie : cookiesLine.split(COOKIE_DELIMITER)) {
            System.out.println(cookie);
            String[] token = cookie.split("=");
            cookies.put(token[0], token[1]);
        }
        return new HttpCookie(cookies);
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String toHttpMessage() {
        return cookies.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
