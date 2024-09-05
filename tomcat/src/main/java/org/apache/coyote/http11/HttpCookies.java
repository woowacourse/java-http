package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private final Map<String, String> cookies;

    public HttpCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader.isBlank()) {
            return new HttpCookies(cookies);
        }
        String[] keyAndValues = cookieHeader.split("; ");
        for (String keyAndValue : keyAndValues) {
            String[] cookie = keyAndValue.split("=");
            cookies.put(cookie[0], cookie[1]);
        }
        return new HttpCookies(cookies);
    }

    public boolean containsSession() {
        return cookies.containsKey("JSESSIONID");
    }
}
