package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private final Map<String, HttpCookie> cookies = new HashMap<>();

    public HttpCookies(String cookieLine) {
        if (cookieLine == null) {
            return;
        }
        String[] cookiePairs = cookieLine.split(";");
        for (String cookiePair : cookiePairs) {
            int index = cookiePair.indexOf("=");
            if (index == -1) {
                break;
            }
            String name = cookiePair.substring(0, index).trim();
            String value = cookiePair.substring(index + 1).trim();
            HttpCookie httpCookie = new HttpCookie(name, value);
            cookies.put(name, httpCookie);
        }
    }

    public void add(HttpCookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public HttpCookie get(String name) {
        return cookies.get(name);
    }
}
