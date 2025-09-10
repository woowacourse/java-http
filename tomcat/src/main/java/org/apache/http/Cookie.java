package org.apache.http;

public class Cookie {

    public static final String SESSION_COOKIE_KEY = "JSESSIONID";
    private final String key;
    private final String value;
    //TODO: 쿠키의 다양한 옵션을 구현하는 것도 고려해보자!  (2025-09-9, 화, 13:19)
    
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
