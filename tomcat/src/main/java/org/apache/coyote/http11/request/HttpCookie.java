package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String requestCookie) {
        final String[] cookies = requestCookie.replace(";", "").split(" ");
        for (String cookie : cookies) {
            final String[] cookieInfo = cookie.split("=");
            final String key = cookieInfo[0];
            final String value = cookieInfo[1];
            this.cookies.put(key, value);
        }
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJsessionid() {
        if (cookies.containsKey(JSESSIONID)) {
            throw new IllegalArgumentException("Session이 존재하지 않습니다.");
        }
        return cookies.get(JSESSIONID);
    }
}
