package org.apache.coyote.http11.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeaderLine) {
        this.cookies = toMap(cookieHeaderLine);
    }

    private Map<String, String> toMap(String cookieHeaderLine) {
        if (cookieHeaderLine == null || cookieHeaderLine.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, String> cookies = new LinkedHashMap<>();
        for (String cookie : cookieHeaderLine.split("; ")) {
            String[] keyAndValue = cookie.split("=");
            cookies.put(keyAndValue[0].trim(), keyAndValue[1].trim());
        }
        return cookies;
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
