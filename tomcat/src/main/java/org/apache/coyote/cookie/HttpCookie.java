package org.apache.coyote.cookie;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookie;

    public HttpCookie(String cookieString) {
        Map<String, String> cookie = new HashMap<>();
        String[] cookiePair = cookieString.split("; ");
        for (String pair : cookiePair) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                cookie.put(key, value);
            }
        }
        this.cookie = cookie;
    }

    public boolean hasJsessionid() {
        return cookie.containsKey("JSESSIONID");
    }
}
