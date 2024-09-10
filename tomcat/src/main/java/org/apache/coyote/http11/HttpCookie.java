package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_NAME_VALUE_DELIMITER = "=";
    private static final int COOKIE_NAME_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> cookie;

    public HttpCookie() {
        this.cookie = new LinkedHashMap<>();
    }

    public void setCookie(String value) {
        for (String cookiePair : value.split(COOKIE_DELIMITER)) {
            String cookieName = cookiePair.split(COOKIE_NAME_VALUE_DELIMITER)[COOKIE_NAME_INDEX];
            String cookieValue = cookiePair.split(COOKIE_NAME_VALUE_DELIMITER)[COOKIE_VALUE_INDEX];
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
