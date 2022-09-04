package org.apache.coyote.http11.common;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookie) {
        return new HttpCookie(HttpParser.parseCookie(cookie));
    }

    public String getSessionId() {
        return values.get("JSESSIONID");
    }
}
