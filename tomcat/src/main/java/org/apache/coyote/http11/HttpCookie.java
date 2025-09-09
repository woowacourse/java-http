package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> httpCookie;

    public HttpCookie(final Map<String, String> httpCookie) {
        this.httpCookie = httpCookie;
    }

    public String getValue(final String key) {
        return httpCookie.get(key);
    }
}
