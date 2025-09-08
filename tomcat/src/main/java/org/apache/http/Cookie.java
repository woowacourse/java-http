package org.apache.http;

public class Cookie {

    public static final String SESSION_COOKIE_KEY = "JSESSIONID";
    private final String key;
    private final String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie makeSessionCookie(String sessionId) {
        return new Cookie(SESSION_COOKIE_KEY, sessionId);
    }

    public String makeCookieLine() {
        return key + "=" + value + ";";
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
