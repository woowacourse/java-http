package org.apache.coyote.http11.cookie;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.MapFactory;

public class HttpCookie {
    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final String ELEMENT_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookie;

    public HttpCookie(String cookieLines) {
        this.cookie = convertToCookie(cookieLines);
    }

    private Map<String, String> convertToCookie(String cookieLines) {
        return MapFactory.create(cookieLines, ELEMENT_SEPARATOR, KEY_VALUE_SEPARATOR);
    }

    public String getJSessionId() {
        return Optional.ofNullable(cookie.get(JSESSIONID_KEY))
                .orElse("");
    }

    public Map<String, String> getCookie() {
        return cookie;
    }
}
