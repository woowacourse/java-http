package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private static final String COOKIE_DELIMITER = ";";

    private final Map<String, String> value;

    public HttpCookie(Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie from(String data) {
        Map<String, String> buffer = new HashMap<>();
        for (String keyValue : data.split(COOKIE_DELIMITER)) {
            String[] cookie = keyValue.split("=");
            String cookieKey = cookie[0];
            String cookieValue = cookie[1];
            buffer.put(cookieKey, cookieValue);
        }
        return new HttpCookie(buffer);
    }

    public String getValue(String key) {
        return value.get(key);
    }
}
