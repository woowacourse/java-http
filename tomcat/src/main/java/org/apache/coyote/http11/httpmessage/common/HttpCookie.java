package org.apache.coyote.http11.httpmessage.common;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parse(final String cookieField) {
        Map<String, String> cookies = new HashMap<>();
        final String[] cookieItems = cookieField.split("; ");
        for (String cookie : cookieItems) {
            String[] splitCookie = cookie.split("=");
            cookies.put(splitCookie[0], splitCookie[1]);
        }
        return new HttpCookie(new HashMap<>(cookies));
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }
}
