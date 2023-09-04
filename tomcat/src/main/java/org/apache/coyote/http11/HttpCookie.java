package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public final Map<String, String> value;

    private HttpCookie(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie of(final String cookies) {
        return new HttpCookie(parse(cookies));
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    private static Map<String, String> parse(final String cookies) {
        if (cookies == null) {
            return new HashMap<>();
        }
        Map<String, String> cookieValue = new HashMap<>();
        for (String cookie : cookies.split("; ")) {
            String[] pair = cookie.split("=");
            cookieValue.put(pair[0], pair[1]);
        }
        return cookieValue;
    }

    public String getCookie(final String key) {
        return value.get(key);
    }
}
