package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private final Map<String, String> cookies;

    public HttpCookies(final String header) {
        this.cookies = parseCookieHeader(header);
    }

    private Map<String, String> parseCookieHeader(final String header) {
        Map<String, String> cookiesStore = new HashMap<>();
        for (String cookie : header.split("; ")) {
            String[] nameValue = cookie.split("=");
            cookiesStore.put(nameValue[0], nameValue[1]);
        }
        return cookiesStore;
    }

    public String getValue(final String name) {
        return cookies.get(name);
    }

    public boolean existSessionId(String sessionId) {
        return cookies.containsKey(sessionId);
    }
}
