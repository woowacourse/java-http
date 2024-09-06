package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = new HashMap<>();
        parseCookies(cookieHeader);
    }

    private void parseCookies(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return;
        }

        String[] cookiePairs = cookieHeader.split(";");

        for (String cookiePair : cookiePairs) {
            String[] keyValue = cookiePair.trim().split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                cookies.put(key, value); // 맵에 저장
            }
        }
    }

    public static String ofJSessionId(String value) {
        return JSESSIONID + "=" + value;
    }

    public String getJsessionid() {
        return getCookie(JSESSIONID);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public boolean containsJSessionId() {
        if (cookies.containsKey(JSESSIONID)) {
            return true;
        }

        return false;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "cookies=" + cookies +
                '}';
    }
}
