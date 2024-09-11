package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie = new HashMap<>();

    public HttpCookie(String cookies) {
        String[] cookie = cookies.split("; ");
        for (String c : cookie) {
            String[] keyAndValue = c.split("=");
            this.cookie.put(keyAndValue[0], keyAndValue[1]);
        }
    }

    public boolean hasKey(String key) {
        return cookie.containsKey(key);
    }
}
