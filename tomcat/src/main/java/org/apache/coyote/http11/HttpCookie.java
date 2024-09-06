package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(String cookie) {
        this.cookies = new HashMap<>();
        add(cookie);
    }

    private void add(String cookie) {
        String[] parts = cookie.split("; ");
        for (String part : parts) {
            String[] parse = part.split("=");
            cookies.put(parse[0], parse[1]);
        }
    }

    public boolean containsJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
