package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeaderContent) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeaderContent == null) {
            return new HttpCookie(cookies);
        }
        String[] cookieHeaders = cookieHeaderContent.split("; ");
        for (String cookieHeader : cookieHeaders) {
            String[] keyValues = cookieHeader.split("=");
            cookies.put(keyValues[0].trim(), keyValues[1].trim());
        }
        return new HttpCookie(cookies);
    }

    public boolean hasNoNamed(final String key) {
        return cookies.get(key) == null;
    }

    public String cookieToString(final String key) {
        String value = cookies.get(key);
        return String.format("%s=%s", key, value);
    }
}
