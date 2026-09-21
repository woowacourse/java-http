package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final String line) {
        for (String cookie : line.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2) {
                cookies.put(pair[0], pair[1]);
            }
        }
    }

    public boolean hasJsessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJsessionId() {
        return cookies.get("JSESSIONID");
    }
}
