package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie(String fieldValue) {
        if (fieldValue == null) {
            this.cookie = new HashMap<>();
            return;
        }
        this.cookie = init(fieldValue);
    }

    private Map<String, String> init(String fieldValue) {
        String[] values = fieldValue.split(";");

        Map<String, String> cookie = new HashMap<>();

        for (String cookieValue : values) {
            String[] keyValue = cookieValue.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            cookie.put(key, value);
        }
        return cookie;
    }

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public String findValue(String key) {
        return cookie.get(key);
    }

}
