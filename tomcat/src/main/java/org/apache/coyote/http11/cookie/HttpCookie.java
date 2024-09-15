package org.apache.coyote.http11.cookie;

import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID_KEY = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getJsessionid() {
        return this.cookies.getOrDefault(JSESSIONID_KEY, "");
    }

    public String getCookieValue(String key) {
        return this.cookies.getOrDefault(key, "");
    }
}
