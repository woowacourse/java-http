package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = HttpParamParser.parseKeyValuePairs(cookieHeader, ";");
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
