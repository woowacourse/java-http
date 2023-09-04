package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final static String BASE_COOKIE = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie of() {
        return parseCookieString(BASE_COOKIE);
    }

    public static HttpCookie parseCookieString(String cookieString) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieString != null && !cookieString.isEmpty()) {
            String[] cookiePairs = cookieString.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.split("=");
                if (parts.length == 2) {
                    String name = parts[0];
                    String value = parts[1];
                    cookies.put(name, value);
                }
            }
        }

        return new HttpCookie(cookies);
    }

    public boolean existJSESSIONID() {
        return cookie.containsKey("JSESSIONID");
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "cookie=" + cookie +
                '}';
    }
}
