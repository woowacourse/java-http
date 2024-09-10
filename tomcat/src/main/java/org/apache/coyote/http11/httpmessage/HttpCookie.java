package org.apache.coyote.http11.httpmessage;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie parseFrom(String cookiesLine) {
        HashMap<String, String> cookies = new HashMap<>();
        for (String cookie : cookiesLine.split("; ")) {
            System.out.println(cookie);
            String[] token = cookie.split("=");
            cookies.put(token[0], token[1]);
        }
        return new HttpCookie(cookies);
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String toHttpMessage() {
        return cookies.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(";"));
    }
}
