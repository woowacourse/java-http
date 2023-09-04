package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader == null) {
            return new HttpCookie(Map.of());
        }
        final Map<String, String> cookies = new HashMap<>();
        final String[] splitHeader = cookieHeader.split("; ");
        for (String value : splitHeader) {
            final String[] splitValues = value.split("=");
            cookies.put(splitValues[0], splitValues[1]);
        }
        return new HttpCookie(cookies);
    }

    public boolean hasCookie(final String cookie) {
        return cookies.containsKey(cookie);
    }
}
