package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpCookie {
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = HttpParamParser.parseKeyValuePairs(cookieHeader, "; ");
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public boolean hasSession() {
        return cookies.containsKey(SESSION_COOKIE_NAME);
    }

    public String getSessionId() {
        return cookies.get(SESSION_COOKIE_NAME);
    }
}
