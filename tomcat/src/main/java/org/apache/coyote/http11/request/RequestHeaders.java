package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RequestHeaders {

    private final Map<String, String> headers;
    private final RequestCookie cookies;

    public RequestHeaders() {
        this.headers = new HashMap<>();
        this.cookies = new RequestCookie();
    }

    public void set(final String key, final String value) {
        if (key.equals(RequestCookie.COOKIE_HEADER_KEY)) {
            setCookie(value);
            return;
        }
        headers.put(key, value);
    }

    private void setCookie(final String cookieString) {
        final StringTokenizer stringTokenizer = new StringTokenizer(cookieString, RequestCookie.COOKIE_DELIMITER);
        while (stringTokenizer.hasMoreTokens()) {
            final String cookieAttribute = stringTokenizer.nextToken();
            cookies.setCookie(cookieAttribute);
        }
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public String getCookie(final String key) {
        return cookies.get(key);
    }

    public boolean contains(final String key) {
        return headers.containsKey(key);
    }

    public boolean containsCookie(final String cookieName) {
        return cookies.contains(cookieName);
    }
}
