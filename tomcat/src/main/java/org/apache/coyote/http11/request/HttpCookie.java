package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> requestCookies;

    public static HttpCookie from(String cookie) {
        Map<String, String> requestCookies = new HashMap<>();
        if (cookie != null) {
            String[] cookies = cookie.split("; ");
            for (int i = 0; i < cookies.length; i++) {
                String[] cookieTokens = cookies[i].split("=");
                requestCookies.put(cookieTokens[0], cookieTokens[1]);
            }
        }
        return new HttpCookie(requestCookies);
    }

    private HttpCookie(Map<String, String> requestCookies) {
        this.requestCookies = requestCookies;
    }

    public boolean isExist(String key) {
        return requestCookies.get(key) != null;
    }

    public String findCookie(String key) {
        return requestCookies.get(key);
    }
}
