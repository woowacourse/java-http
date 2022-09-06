package org.apache.coyote.http11.cookie;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public Map<String, String> getCookie() {
        return cookie;
    }
}
