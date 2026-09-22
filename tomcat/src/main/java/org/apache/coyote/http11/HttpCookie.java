package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpCookie {

    private final Map<String, String> httpCookies;

    private HttpCookie(Map<String, String> httpCookies) {
        this.httpCookies = httpCookies;
    }

    public static HttpCookie from(String cookieLine) {
        Map<String, String> cookies = extractCookies(cookieLine);
        return new HttpCookie(cookies);
    }

    private static Map<String, String> extractCookies(String cookieLine) {
        if (cookieLine != null && !cookieLine.isEmpty()) {
            return createCookies(cookieLine);
        }
        return Map.of();
    }

    private static Map<String, String> createCookies(String cookieLine) {
        Map<String, String> cookies = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(cookieLine, ";");
        while (stringTokenizer.hasMoreTokens()) {
            String cookie = stringTokenizer.nextToken().strip();
            addCookie(cookies, cookie);
        }
        return cookies;
    }

    private static void addCookie(Map<String, String> cookies, String cookie) {
        String[] cookieKeyValue = cookie.split("=", 2);
        if (cookieKeyValue.length != 2) {
            return;
        }
        cookies.put(cookieKeyValue[0], cookieKeyValue[1]);
    }

    public String getJsessionid() {
        String jsessionid = httpCookies.get("JSESSIONID");
        if (jsessionid == null || jsessionid.isEmpty()) {
            return "";
        }
        return jsessionid;
    }
}
