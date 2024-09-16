package org.apache.coyote.http11.message.common;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String EMPTY = "";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public boolean hasSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getSessionId() {
        return cookies.getOrDefault(JSESSIONID, EMPTY);
    }
}
