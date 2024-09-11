package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Map<String, HttpCookie> getCookies() {
        return cookies;
    }

    public Optional<HttpCookie> get(String name) {
        return Optional.of(cookies.get(name));
    }
}
