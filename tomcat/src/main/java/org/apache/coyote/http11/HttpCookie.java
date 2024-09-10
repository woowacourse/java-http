package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> cookie;

    public HttpCookie() {
        this.cookie = new LinkedHashMap<>();
    }

    public void setCookie(String value) {
        for (String cookiePair : value.split(COOKIE_DELIMITER)) {
            String cookieName = cookiePair.split(COOKIE_NAME_VALUE_DELIMITER)[0];
            String cookieValue = cookiePair.split(COOKIE_NAME_VALUE_DELIMITER)[1];
            cookie.put(cookieName, cookieValue);
        }
    }

    public boolean isExists(String key) {
        return cookie.containsKey(key);
    }

    public String getValue(String key) {
        return cookie.get(key);
    }
}
